import React, { useState, useEffect } from 'react';
import { Container, Tabs, Tab, Row, Col, Spinner, Alert } from 'react-bootstrap';
import EventCard from '../components/event/EventCard';
import api from '../services/api';

const Events = () => {
    const [activeEvents, setActiveEvents] = useState([]);
    const [closedEvents, setClosedEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [key, setKey] = useState('active');

    useEffect(() => {
        const fetchEvents = async () => {
            try {
                setLoading(true);
                const activeRes = await api.get('/events', { params: { status: 'ACTIVE', size: 100 } });
                const closedRes = await api.get('/events', { params: { status: 'CLOSED', size: 100 } });

                setActiveEvents(activeRes.data.content);
                setClosedEvents(closedRes.data.content);
            } catch (err) {
                console.error('Failed to fetch events', err);
                setError('이벤트 목록을 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchEvents();
    }, []);

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" role="status" />
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="py-5">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <h2 className="fw-bold mb-4 text-center">이벤트</h2>

            <Tabs
                id="event-tabs"
                activeKey={key}
                onSelect={(k) => setKey(k)}
                className="mb-4 justify-content-center"
                variant="pills"
            >
                <Tab eventKey="active" title="진행중인 이벤트">
                    {activeEvents.length === 0 ? (
                        <div className="text-center py-5 text-muted">진행중인 이벤트가 없습니다.</div>
                    ) : (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {activeEvents.map(event => (
                                <Col key={event.id}>
                                    <EventCard event={event} />
                                </Col>
                            ))}
                        </Row>
                    )}
                </Tab>
                <Tab eventKey="closed" title="종료된 이벤트">
                    {closedEvents.length === 0 ? (
                        <div className="text-center py-5 text-muted">종료된 이벤트가 없습니다.</div>
                    ) : (
                        <Row xs={1} md={2} lg={3} className="g-4">
                            {closedEvents.map(event => (
                                <Col key={event.id}>
                                    <EventCard event={event} />
                                </Col>
                            ))}
                        </Row>
                    )}
                </Tab>
            </Tabs>
        </Container>
    );
};

export default Events;
