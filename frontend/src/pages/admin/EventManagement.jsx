import React, { useState, useEffect } from 'react';
import { Table, Button, Badge, Container, Modal, Form } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const EventManagement = () => {
    const navigate = useNavigate();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showDrawModal, setShowDrawModal] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);
    const [drawCount, setDrawCount] = useState(3);
    const [winners, setWinners] = useState([]);

    useEffect(() => {
        fetchEvents();
    }, []);

    const fetchEvents = async () => {
        try {
            setLoading(true);
            // Admin should see all events ideally, logic in backend might need check
            // Our updated backend logic returns all if status param is empty.
            // And default Pageable default sort is startAt.
            const response = await api.get('/events', { params: { size: 100, sort: 'id,desc' } });
            setEvents(response.data.content);
        } catch (err) {
            console.error('Failed to fetch events', err);
            alert('이벤트 목록 로드 실패');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('정말 삭제하시겠습니까?')) {
            try {
                await api.delete(`/events/${id}`);
                fetchEvents();
            } catch (err) {
                console.error('Delete failed', err);
                alert('삭제 실패');
            }
        }
    };

    const handleDrawClick = (id) => {
        setSelectedEventId(id);
        setShowDrawModal(true);
        setWinners([]);
    };

    const handleDrawSubmit = async () => {
        try {
            const response = await api.post(`/events/${selectedEventId}/draw`, { count: parseInt(drawCount) });
            setWinners(response.data);
            alert('추첨이 완료되었습니다!');
            // Ideally refresh event to show it's drawn or similar if status changes
        } catch (err) {
            console.error('Draw failed', err);
            alert('추첨 실패: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <Container fluid>
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>이벤트 관리</h2>
                <Button onClick={() => navigate('/admin/events/new')}>새 이벤트 등록</Button>
            </div>

            <Table striped bordered hover responsive>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>제목</th>
                        <th>기간</th>
                        <th>상태</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {events.map(event => (
                        <tr key={event.id}>
                            <td>{event.id}</td>
                            <td>{event.title}</td>
                            <td>
                                {new Date(event.startAt).toLocaleDateString()} ~
                                {new Date(event.endAt).toLocaleDateString()}
                            </td>
                            <td>
                                <Badge bg={event.status === 'ACTIVE' ? 'success' : event.status === 'CLOSED' ? 'dark' : 'secondary'}>
                                    {event.status}
                                </Badge>
                            </td>
                            <td>
                                <Button size="sm" variant="outline-primary" className="me-2"
                                    onClick={() => navigate(`/admin/events/${event.id}/edit`)}>
                                    수정
                                </Button>
                                <Button size="sm" variant="outline-danger" className="me-2"
                                    onClick={() => handleDelete(event.id)}>
                                    삭제
                                </Button>
                                {event.status === 'CLOSED' && (
                                    <Button size="sm" variant="warning"
                                        onClick={() => handleDrawClick(event.id)}>
                                        추첨
                                    </Button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>

            <Modal show={showDrawModal} onHide={() => setShowDrawModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>당첨자 추첨</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Group className="mb-3">
                        <Form.Label>추첨 인원</Form.Label>
                        <Form.Control
                            type="number"
                            value={drawCount}
                            onChange={(e) => setDrawCount(e.target.value)}
                            min="1"
                        />
                    </Form.Group>

                    {winners.length > 0 && (
                        <div className="mt-3">
                            <h5>당첨자 명단:</h5>
                            <ul>
                                {winners.map(w => (
                                    <li key={w.participantId}>{w.user.name} ({w.user.username})</li>
                                ))}
                            </ul>
                        </div>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDrawModal(false)}>
                        닫기
                    </Button>
                    <Button variant="primary" onClick={handleDrawSubmit} disabled={winners.length > 0}>
                        추첨하기
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
};

export default EventManagement;
