import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Badge, Alert, Spinner } from 'react-bootstrap';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const Ideas = () => {
    const { user } = useAuth();
    const [ideas, setIdeas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showForm, setShowForm] = useState(false);

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [submitLoading, setSubmitLoading] = useState(false);
    const [submitResult, setSubmitResult] = useState({ variant: '', message: '' });

    useEffect(() => {
        fetchIdeas();
    }, []);

    const fetchIdeas = async () => {
        try {
            const response = await api.get('/ideas');
            setIdeas(response.data.content); // Page response
            setLoading(false);
        } catch (err) {
            console.error('Failed to fetch ideas:', err);
            setError('제안 목록을 불러오는 중 오류가 발생했습니다.');
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitLoading(true);
        setSubmitResult({ variant: '', message: '' });

        if (!title.trim() || !description.trim()) {
            setSubmitResult({ variant: 'warning', message: '제목과 내용을 모두 입력해주세요.' });
            setSubmitLoading(false);
            return;
        }

        try {
            await api.post('/ideas', { title, description });
            setSubmitResult({ variant: 'success', message: '아이디어가 성공적으로 등록되었습니다.' });
            setTitle('');
            setDescription('');
            setShowForm(false);
            fetchIdeas(); // Refresh list
        } catch (err) {
            console.error('Failed to submit idea:', err);
            setSubmitResult({ variant: 'danger', message: '아이디어 등록에 실패했습니다.' });
        } finally {
            setSubmitLoading(false);
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

    if (loading) return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
            <Spinner animation="border" variant="primary" />
        </Container>
    );

    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2>아이디어 제안</h2>
                    <p className="text-muted">더 좋은 사보를 만들기 위한 아이디어를 자유롭게 제안해주세요.</p>
                </div>
                {!showForm && (
                    <Button variant="primary" onClick={() => setShowForm(true)}>
                        새 아이디어 제안
                    </Button>
                )}
            </div>

            {submitResult.message && (
                <Alert variant={submitResult.variant} onClose={() => setSubmitResult({ variant: '', message: '' })} dismissible>
                    {submitResult.message}
                </Alert>
            )}

            {showForm && (
                <Card className="mb-4 shadow-sm border-primary">
                    <Card.Header className="bg-primary text-white">새 아이디어 작성</Card.Header>
                    <Card.Body>
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Label>제목</Form.Label>
                                <Form.Control
                                    type="text"
                                    placeholder="아이디어 제목을 입력하세요"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>내용</Form.Label>
                                <Form.Control
                                    as="textarea"
                                    rows={5}
                                    placeholder="구체적인 아이디어 내용을 입력하세요"
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                />
                            </Form.Group>

                            <div className="d-flex justify-content-end gap-2">
                                <Button variant="secondary" onClick={() => setShowForm(false)}>취소</Button>
                                <Button variant="primary" type="submit" disabled={submitLoading}>
                                    {submitLoading ? '등록 중...' : '등록하기'}
                                </Button>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            )}

            <h4 className="mb-3">내 제안 목록</h4>
            {ideas.length === 0 ? (
                <Alert variant="light" className="text-center py-5 text-muted">
                    아직 등록된 제안이 없습니다. 첫 번째 아이디어를 제안해보세요!
                </Alert>
            ) : (
                <Row>
                    {ideas.map((idea) => (
                        <Col md={12} className="mb-3" key={idea.ideaId}>
                            <Card className="shadow-sm">
                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <Card.Title className="mb-0">{idea.title}</Card.Title>
                                        {getStatusBadge(idea.status)}
                                    </div>
                                    <Card.Text className="text-muted mb-2" style={{ whiteSpace: 'pre-line' }}>
                                        {idea.description}
                                    </Card.Text>

                                    {idea.adminComment && (
                                        <Alert variant="secondary" className="mt-3 mb-0 text-sm">
                                            <strong>관리자 코멘트:</strong> {idea.adminComment}
                                        </Alert>
                                    )}

                                    <div className="text-end text-muted small mt-2">
                                        {new Date(idea.createdAt).toLocaleDateString()}
                                    </div>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}
        </Container>
    );
};

export default Ideas;
