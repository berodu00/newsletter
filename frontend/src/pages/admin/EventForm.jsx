import React, { useState, useEffect } from 'react';
import { Form, Button, Container, Spinner, Alert } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import api from '../../services/api';

const EventForm = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEditMode = !!id;

    const [formData, setFormData] = useState({
        title: '',
        description: '',
        startAt: '',
        endAt: '',
        status: 'INACTIVE',
        thumbnailFileId: null // Assuming interaction with file upload returns ID
    });
    const [file, setFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isEditMode) {
            fetchEvent();
        }
    }, [id]);

    const fetchEvent = async () => {
        try {
            const response = await api.get(`/events/${id}`);
            const event = response.data;
            setFormData({
                title: event.title,
                description: event.description,
                startAt: event.startAt.slice(0, 16), // Format for datetime-local
                endAt: event.endAt.slice(0, 16),
                status: event.status,
                thumbnailFileId: event.thumbnailFile?.fileId
            });
        } catch (err) {
            console.error('Fetch event failed', err);
            setError('이벤트 정보 로드 실패');
        }
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleUpload = async () => {
        if (!file) return null;
        const formData = new FormData();
        formData.append('file', file);
        try {
            const res = await api.post('/files/upload', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            return res.data.fileId;
        } catch (err) {
            console.error('File upload failed', err);
            throw new Error('파일 업로드 실패');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            let thumbnailFileId = formData.thumbnailFileId;
            if (file) {
                thumbnailFileId = await handleUpload();
            }

            const payload = {
                ...formData,
                thumbnailFileId,
                startAt: new Date(formData.startAt).toISOString(),
                endAt: new Date(formData.endAt).toISOString()
            };

            if (isEditMode) {
                await api.put(`/events/${id}`, payload);
            } else {
                await api.post('/events', payload);
            }
            navigate('/admin/events');
        } catch (err) {
            console.error('Submit failed', err);
            setError('저장 실패: ' + (err.response?.data?.message || err.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container>
            <h2 className="mb-4">{isEditMode ? '이벤트 수정' : '새 이벤트 등록'}</h2>
            {error && <Alert variant="danger">{error}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>제목</Form.Label>
                    <Form.Control
                        type="text"
                        value={formData.title}
                        onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>설명</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={10}
                        value={formData.description}
                        onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>썸네일 이미지</Form.Label>
                    <Form.Control type="file" onChange={handleFileChange} />
                    {formData.thumbnailFileId && !file && <div className="text-muted small mt-1">기존 이미지 유지됨</div>}
                </Form.Group>

                <div className="row">
                    <div className="col-md-6 mb-3">
                        <Form.Label>시작 일시</Form.Label>
                        <Form.Control
                            type="datetime-local"
                            value={formData.startAt}
                            onChange={(e) => setFormData({ ...formData, startAt: e.target.value })}
                            required
                        />
                    </div>
                    <div className="col-md-6 mb-3">
                        <Form.Label>종료 일시</Form.Label>
                        <Form.Control
                            type="datetime-local"
                            value={formData.endAt}
                            onChange={(e) => setFormData({ ...formData, endAt: e.target.value })}
                            required
                        />
                    </div>
                </div>

                <Form.Group className="mb-3">
                    <Form.Label>상태</Form.Label>
                    <Form.Select
                        value={formData.status}
                        onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    >
                        <option value="INACTIVE">준비중 (INACTIVE)</option>
                        <option value="ACTIVE">진행중 (ACTIVE)</option>
                        <option value="CLOSED">종료 (CLOSED)</option>
                    </Form.Select>
                </Form.Group>

                <Button variant="primary" type="submit" disabled={loading}>
                    {loading ? <Spinner as="span" animation="border" size="sm" /> : '저장'}
                </Button>
                <Button variant="secondary" className="ms-2" onClick={() => navigate('/admin/events')}>
                    취소
                </Button>
            </Form>
        </Container>
    );
};

export default EventForm;
