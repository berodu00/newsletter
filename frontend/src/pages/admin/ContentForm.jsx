import React, { useState, useEffect } from 'react';
import { Container, Form, Button, Alert, Row, Col, Spinner, Image } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import api from '../../services/api';

const ContentForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = !!id;

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Form State
    const [title, setTitle] = useState('');
    const [summary, setSummary] = useState('');
    const [bodyHtml, setBodyHtml] = useState('');
    const [categoryName, setCategoryName] = useState('Special'); // Default
    const [status, setStatus] = useState('DRAFT');
    const [hashtags, setHashtags] = useState('');
    const [thumbnailFile, setThumbnailFile] = useState(null);
    const [thumbnailPreview, setThumbnailPreview] = useState(null);
    const [thumbnailFileId, setThumbnailFileId] = useState(null);

    useEffect(() => {
        if (isEditMode) {
            fetchContent();
        }
    }, [id]);

    const fetchContent = async () => {
        try {
            setLoading(true);
            const response = await api.get(`/contents/${id}`);
            const data = response.data;
            setTitle(data.title);
            setSummary(data.summary || '');
            setBodyHtml(data.bodyHtml || '');
            setCategoryName(data.categoryName);
            setStatus(data.status || 'DRAFT'); // status not usually in public detail, but assuming admin API returns it or use management list for it. 
            // Detail response from public api might NOT have status if DRAFT. 
            // Plan 2.2 says '/api/contents/{id}' is detail.
            // If we are admin, we should be able to get it. 
            // Let's assume Admin can see fields.

            // Hashtags array to string
            if (data.hashtags) {
                setHashtags(data.hashtags.map(t => t.hashtagName || t).join(', '));
            }
            if (data.thumbnailUrl) {
                setThumbnailPreview(data.thumbnailUrl);
                // thumbnailFileId is hard to retrieve unless API returns it. 
                // For now, if we don't upload new, keep existing.
                // API should handle updating only changed fields if we use PATCH or if we send existing ID.
                // Our PUT replaces everything or updates? `ContentService` update checks nulls.
            }
        } catch (err) {
            console.error('Fetch error', err);
            setError('컨텐츠 정보를 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setThumbnailFile(file);
            setThumbnailPreview(URL.createObjectURL(file));
            // Upload immediately or on submit? Plan says "File upload on select" in Plan 2.10 logic? 
            // Plan: "File Upload ... POST /api/files/upload ... Receive fileId". 
            // Let's do it immediately for UX feedback or simplicity.
            uploadFile(file);
        }
    };

    const uploadFile = async (file) => {
        try {
            const formData = new FormData();
            formData.append('file', file);
            const res = await api.post('/files/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            setThumbnailFileId(res.data.fileId);
        } catch (err) {
            console.error('File upload failed', err);
            alert('파일 업로드 실패');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            // 1. Prepare Payload
            const payload = {
                title,
                summary,
                bodyHtml,
                bodyText: bodyHtml.replace(/<[^>]*>?/gm, ''), // Simple text extraction for search
                categoryName,
                status,
                hashtags: hashtags.split(',').map(tag => tag.trim()).filter(t => t),
                thumbnailFileId: thumbnailFileId // If null, service might ignore or clear? 
                // Service update logic: `if (dto.getThumbnailFileId() != null)`
                // So if we don't upload new, it keeps old. Good.
            };

            if (isEditMode) {
                await api.put(`/contents/${id}`, payload);
            } else {
                await api.post('/contents', payload);
            }

            navigate('/admin/contents');
        } catch (err) {
            console.error('Submit failed', err);
            setError('저장에 실패했습니다.');
            setLoading(false);
        }
    };

    if (loading && isEditMode && !title) {
        return <Container className="py-5 text-center"><Spinner animation="border" /></Container>;
    }

    return (
        <Container className="py-5">
            <h2 className="mb-4 fw-bold">{isEditMode ? '컨텐츠 수정' : '새 컨텐츠 작성'}</h2>
            {error && <Alert variant="danger">{error}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Row className="mb-3">
                    <Col md={8}>
                        <Form.Group className="mb-3">
                            <Form.Label>제목</Form.Label>
                            <Form.Control
                                type="text"
                                value={title}
                                onChange={e => setTitle(e.target.value)}
                                required
                            />
                        </Form.Group>
                    </Col>
                    <Col md={4}>
                        <Form.Group className="mb-3">
                            <Form.Label>상태</Form.Label>
                            <Form.Select value={status} onChange={e => setStatus(e.target.value)}>
                                <option value="DRAFT">임시저장 (DRAFT)</option>
                                <option value="PUBLISHED">발행 (PUBLISHED)</option>
                                <option value="SCHEDULED">예약 (SCHEDULED)</option>
                                <option value="ARCHIVED">보관 (ARCHIVED)</option>
                            </Form.Select>
                        </Form.Group>
                    </Col>
                </Row>

                <Row className="mb-3">
                    <Col md={6}>
                        <Form.Group className="mb-3">
                            <Form.Label>카테고리</Form.Label>
                            <Form.Select value={categoryName} onChange={e => setCategoryName(e.target.value)}>
                                {/* Hardcoded for now as per plan */}
                                <option value="Special">Special</option>
                                <option value="People">People</option>
                                <option value="Life">Life</option>
                            </Form.Select>
                        </Form.Group>
                    </Col>
                    <Col md={6}>
                        <Form.Group className="mb-3">
                            <Form.Label>해시태그 (쉼표로 구분)</Form.Label>
                            <Form.Control
                                type="text"
                                value={hashtags}
                                onChange={e => setHashtags(e.target.value)}
                                placeholder="예: 뉴스, 행사, 공지"
                            />
                        </Form.Group>
                    </Col>
                </Row>

                <Form.Group className="mb-3">
                    <Form.Label>요약</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={2}
                        value={summary}
                        onChange={e => setSummary(e.target.value)}
                    />
                </Form.Group>

                <Form.Group className="mb-4">
                    <Form.Label>썸네일 이미지</Form.Label>
                    <Form.Control type="file" onChange={handleFileChange} accept="image/*" />
                    {thumbnailPreview && (
                        <div className="mt-2">
                            <Image src={thumbnailPreview} thumbnail style={{ maxHeight: '200px' }} />
                        </div>
                    )}
                </Form.Group>

                <Form.Group className="mb-4">
                    <Form.Label>본문 (HTML)</Form.Label>
                    <ReactQuill
                        theme="snow"
                        value={bodyHtml}
                        onChange={setBodyHtml}
                        style={{ height: '300px', marginBottom: '50px' }}
                    />
                </Form.Group>

                <div className="d-flex gap-2 justify-content-end">
                    <Button variant="secondary" onClick={() => navigate('/admin/contents')}>
                        취소
                    </Button>
                    <Button variant="primary" type="submit" disabled={loading}>
                        {loading ? '저장 중...' : '저장하기'}
                    </Button>
                </div>
            </Form>
        </Container>
    );
};

export default ContentForm;
