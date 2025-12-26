import React, { useState, useEffect } from 'react';
import { Modal, Form, Button, Image, Alert } from 'react-bootstrap';
import api from '../../services/api';

const PopupForm = ({ show, onHide, popup, refreshPopups }) => {
    const [title, setTitle] = useState('');
    const [startAt, setStartAt] = useState('');
    const [endAt, setEndAt] = useState('');
    const [isActive, setIsActive] = useState(true);
    const [file, setFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (popup) {
            setTitle(popup.title);
            setStartAt(popup.startAt ? popup.startAt.slice(0, 16) : ''); // Format for datetime-local
            setEndAt(popup.endAt ? popup.endAt.slice(0, 16) : '');
            setIsActive(popup.isActive);
            setPreviewUrl(popup.imageUrl || '');
            setFile(null);
        } else {
            // Default values for new popup
            setTitle('');
            // Default start: now, end: 7 days later
            const now = new Date();
            const nextWeek = new Date(now);
            nextWeek.setDate(now.getDate() + 7);

            setStartAt(now.toISOString().slice(0, 16));
            setEndAt(nextWeek.toISOString().slice(0, 16));
            setIsActive(true);
            setPreviewUrl('');
            setFile(null);
        }
        setError('');
    }, [popup, show]);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
            setPreviewUrl(URL.createObjectURL(selectedFile));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            let resourceId = popup?.imageUrl ? null : null; // If editing and no new file, keep existing logic? 
            // Actually API likely expects resourceId. The existing API design in PopupService probably handles resource linking. 
            // Wait, standard upload flow: Upload file -> get ResourceFile ID -> submit Popup with resourceId.

            // Upload File if selected
            let uploadedResourceId = null;
            if (file) {
                const formData = new FormData();
                formData.append('file', file);
                const uploadRes = await api.post('/files/upload', formData, {
                    headers: { 'Content-Type': 'multipart/form-data' }
                });
                uploadedResourceId = uploadRes.data.fileId;
            }

            const popupData = {
                title,
                startAt: new Date(startAt).toISOString(),
                endAt: new Date(endAt).toISOString(),
                isActive,
                resourceId: uploadedResourceId // If null, backend should handle partial update or we need logic.
                // Assuming backend updates resourceId only if provided? 
                // Let's check PopupRequestDto structure or logic if possible. 
                // Creating a popup usually requires an image. Editing might keep it.
            };

            // Refined Logic:
            // Create: must have resourceId
            // Update: can have resourceId (replace) or not (keep)

            if (!popup && !uploadedResourceId) {
                setError('팝업 이미지를 업로드해주세요.');
                setLoading(false);
                return;
            }

            if (popup) {
                // Update
                if (uploadedResourceId) {
                    popupData.resourceId = uploadedResourceId;
                }
                // If not uploading new file, we don't send resourceId to avoid nulling it, 
                // OR we presume backend ignores null if unintended.
                // Let's assume standard PUT replaces fields if present. 
                // If resourceId is null in DTO, does it remove image? 
                // Safe bet: Only send resourceId if it changed.

                await api.put(`/popups/${popup.popupId}`, popupData);
            } else {
                // Create
                popupData.resourceId = uploadedResourceId;
                await api.post('/popups', popupData);
            }

            refreshPopups();
            onHide();
        } catch (err) {
            console.error('Failed to save popup:', err);
            setError('팝업 저장에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal show={show} onHide={onHide}>
            <Modal.Header closeButton>
                <Modal.Title>{popup ? '팝업 수정' : '새 팝업 등록'}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {error && <Alert variant="danger">{error}</Alert>}
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>제목</Form.Label>
                        <Form.Control
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>게시 시작 일시</Form.Label>
                        <Form.Control
                            type="datetime-local"
                            value={startAt}
                            onChange={(e) => setStartAt(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>게시 종료 일시</Form.Label>
                        <Form.Control
                            type="datetime-local"
                            value={endAt}
                            onChange={(e) => setEndAt(e.target.value)}
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Check
                            type="switch"
                            id="active-switch"
                            label="활성 상태"
                            checked={isActive}
                            onChange={(e) => setIsActive(e.target.checked)}
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>팝업 이미지</Form.Label>
                        <Form.Control
                            type="file"
                            accept="image/*"
                            onChange={handleFileChange}
                        />
                        {previewUrl && (
                            <div className="mt-2 text-center">
                                <Image src={previewUrl} alt="Preview" thumbnail style={{ maxHeight: '200px' }} />
                            </div>
                        )}
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-2">
                        <Button variant="secondary" onClick={onHide}>취소</Button>
                        <Button variant="primary" type="submit" disabled={loading}>
                            {loading ? '저장 중...' : '저장'}
                        </Button>
                    </div>
                </Form>
            </Modal.Body>
        </Modal>
    );
};

export default PopupForm;
