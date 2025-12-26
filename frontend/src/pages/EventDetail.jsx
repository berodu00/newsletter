import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Button, Spinner, Alert, Form, Card } from 'react-bootstrap';
import api from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const EventDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const [event, setEvent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [participating, setParticipating] = useState(false);
    const [comment, setComment] = useState('');
    const [winners, setWinners] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchEvent = async () => {
            try {
                setLoading(true);
                const response = await api.get(`/events/${id}`);
                setEvent(response.data);

                if (response.data.status === 'CLOSED') {
                    const winnersRes = await api.get(`/events/${id}/winners`);
                    setWinners(winnersRes.data);
                }
            } catch (err) {
                console.error('Failed to fetch event', err);
                setError('이벤트 정보를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchEvent();
    }, [id]);

    const handleParticipate = async (e) => {
        e.preventDefault();
        if (!user) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }

        try {
            setParticipating(true);
            await api.post(`/events/${id}/participate`, { comment });
            alert('이벤트 참여가 완료되었습니다!');
            setComment('');
        } catch (err) {
            console.error('Participate failed', err);
            alert('이미 참여했거나 참여할 수 없는 상태입니다.');
        } finally {
            setParticipating(false);
        }
    };

    if (loading) return <Container className="py-5 text-center"><Spinner animation="border" /></Container>;
    if (error) return <Container className="py-5"><Alert variant="danger">{error}</Alert></Container>;
    if (!event) return null;

    const isClosed = event.status === 'CLOSED';

    return (
        <Container className="py-5" style={{ maxWidth: '800px' }}>
            <div className="mb-4">
                {/* Thumbnail Image */}
                <div className="mb-4" style={{ height: '300px', backgroundColor: '#f8f9fa', overflow: 'hidden', borderRadius: '8px' }}>
                    {event.thumbnailFile ? (
                        <img
                            src={`/uploads/${event.thumbnailFile.storedName}`}
                            alt={event.title}
                            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                    ) : (
                        <div className="d-flex align-items-center justify-content-center h-100 text-muted">
                            이미지가 없습니다
                        </div>
                    )}
                </div>

                <h1 className="fw-bold mb-3">{event.title}</h1>
                <div className="text-muted mb-4">
                    기간: {new Date(event.startAt).toLocaleDateString()} ~ {new Date(event.endAt).toLocaleDateString()}
                </div>

                <hr className="my-4" />

                <div className="mb-5" style={{ whiteSpace: 'pre-line' }}>
                    {event.description}
                </div>

                {/* Participation Section */}
                {!isClosed && event.status === 'ACTIVE' && (
                    <Card className="bg-light border-0 p-4 mb-5">
                        <h4 className="fw-bold mb-3">이벤트 참여하기</h4>
                        <Form onSubmit={handleParticipate}>
                            <Form.Group className="mb-3">
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    placeholder="기대평이나 정답을 입력해주세요."
                                    value={comment}
                                    onChange={(e) => setComment(e.target.value)}
                                    required
                                />
                            </Form.Group>
                            <div className="d-grid">
                                <Button variant="primary" size="lg" type="submit" disabled={participating}>
                                    {participating ? '참여 중...' : '참여하기'}
                                </Button>
                            </div>
                        </Form>
                    </Card>
                )}

                {/* Winners Section */}
                {isClosed && (
                    <div className="mt-5">
                        <h4 className="fw-bold mb-4 text-center">🏆 당첨자 발표 🏆</h4>
                        {winners.length > 0 ? (
                            <div className="row g-3 justify-content-center">
                                {winners.map(winner => (
                                    <div key={winner.participantId} className="col-auto">
                                        <div className="border rounded px-4 py-2 bg-warning bg-opacity-10 text-center">
                                            <strong>{winner.user.name}</strong> 님
                                        </div>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p className="text-center text-muted">당첨자 정보가 없습니다.</p>
                        )}
                    </div>
                )}
            </div>

            <div className="text-center">
                <Button variant="outline-secondary" onClick={() => navigate('/events')}>
                    목록으로 돌아가기
                </Button>
            </div>
        </Container>
    );
};

export default EventDetail;
