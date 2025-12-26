import React, { useState, useEffect } from 'react';
import { Container, Table, Button, Badge, Modal, Form, Alert } from 'react-bootstrap';
import api from '../../services/api';

const IdeaManagement = () => {
    const [ideas, setIdeas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [selectedIdea, setSelectedIdea] = useState(null);
    const [comment, setComment] = useState('');
    const [status, setStatus] = useState('REVIEWED');
    const [actionLoading, setActionLoading] = useState(false);

    useEffect(() => {
        fetchIdeas();
    }, []);

    const fetchIdeas = async () => {
        try {
            const response = await api.get('/ideas'); // Admin gets all ideas
            setIdeas(response.data.content);
            setLoading(false);
        } catch (error) {
            console.error('Failed to fetch ideas:', error);
            setLoading(false);
        }
    };

    const handleReviewClick = (idea) => {
        setSelectedIdea(idea);
        setStatus(idea.status === 'PENDING' ? 'REVIEWED' : idea.status);
        setComment(idea.adminComment || '');
        setShowModal(true);
    };

    const handleSaveReview = async () => {
        if (!selectedIdea) return;
        setActionLoading(true);
        try {
            await api.put(`/ideas/${selectedIdea.ideaId}/review`, {
                status,
                adminComment: comment
            });
            setShowModal(false);
            fetchIdeas(); // Refresh
        } catch (error) {
            console.error('Failed to review idea:', error);
            alert('저장 실패');
        } finally {
            setActionLoading(false);
        }
    };

    const getStatusBadge = (status) => {
        switch (status) {
            case 'PENDING': return <Badge bg="secondary">검토 대기</Badge>;
            case 'REVIEWED': return <Badge bg="info">검토 중</Badge>;
            case 'ACCEPTED': return <Badge bg="success">채택됨</Badge>;
            case 'REJECTED': return <Badge bg="danger">반려됨</Badge>;
            default: return <Badge bg="secondary">{status}</Badge>;
        }
    };

    if (loading) return <div>Loading...</div>;

    return (
        <Container className="py-4">
            <h2 className="mb-4">아이디어 제안 관리</h2>
            <Table striped bordered hover responsive>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>상태</th>
                        <th>작성자</th>
                        <th>제목</th>
                        <th>등록일</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {ideas.map((idea) => (
                        <tr key={idea.ideaId}>
                            <td>{idea.ideaId}</td>
                            <td>{getStatusBadge(idea.status)}</td>
                            <td>{idea.authorName}</td>
                            <td>{idea.title}</td>
                            <td>{new Date(idea.createdAt).toLocaleDateString()}</td>
                            <td>
                                <Button variant="outline-primary" size="sm" onClick={() => handleReviewClick(idea)}>
                                    검토
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>

            <Modal show={showModal} onHide={() => setShowModal(false)} size="lg">
                <Modal.Header closeButton>
                    <Modal.Title>아이디어 검토</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedIdea && (
                        <>
                            <div className="mb-4">
                                <h5>{selectedIdea.title}</h5>
                                <p className="text-muted mb-1">작성자: {selectedIdea.authorName} | 작성일: {new Date(selectedIdea.createdAt).toLocaleString()}</p>
                                <hr />
                                <p style={{ whiteSpace: 'pre-wrap' }}>{selectedIdea.description}</p>
                            </div>

                            <Form>
                                <Form.Group className="mb-3">
                                    <Form.Label>상태 변경</Form.Label>
                                    <Form.Select value={status} onChange={(e) => setStatus(e.target.value)}>
                                        <option value="PENDING">검토 대기</option>
                                        <option value="REVIEWED">검토 중</option>
                                        <option value="ACCEPTED">채택</option>
                                        <option value="REJECTED">반려</option>
                                    </Form.Select>
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>관리자 코멘트</Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        value={comment}
                                        onChange={(e) => setComment(e.target.value)}
                                        placeholder="검토 의견을 입력하세요"
                                    />
                                </Form.Group>
                            </Form>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowModal(false)}>취소</Button>
                    <Button variant="primary" onClick={handleSaveReview} disabled={actionLoading}>
                        {actionLoading ? '저장 중...' : '저장'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default IdeaManagement;
