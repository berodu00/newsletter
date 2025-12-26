import React from 'react';
import { Card, Badge, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const EventCard = ({ event }) => {
    const navigate = useNavigate();

    const handleCardClick = () => {
        navigate(`/events/${event.id}`);
    };

    const statusBadge = () => {
        switch (event.status) {
            case 'ACTIVE': return <Badge bg="success">진행중</Badge>;
            case 'INACTIVE': return <Badge bg="secondary">준비중</Badge>;
            case 'CLOSED': return <Badge bg="dark">종료</Badge>;
            default: return null;
        }
    };

    const startDate = new Date(event.startAt).toLocaleDateString();
    const endDate = new Date(event.endAt).toLocaleDateString();

    return (
        <Card className="h-100 shadow-sm" style={{ cursor: 'pointer' }} onClick={handleCardClick}>
            <div style={{ height: '200px', backgroundColor: '#e9ecef', overflow: 'hidden' }}>
                {event.thumbnailFile ? (
                    <Card.Img
                        variant="top"
                        src={`/uploads/${event.thumbnailFile.storedName}`} // Assuming file path/structure
                        alt={event.title}
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                    />
                ) : (
                    <div className="d-flex align-items-center justify-content-center h-100 text-muted">
                        No Image
                    </div>
                )}
            </div>
            <Card.Body>
                <div className="mb-2">{statusBadge()}</div>
                <Card.Title className="text-truncate" title={event.title}>{event.title}</Card.Title>
                <Card.Text className="text-muted small">
                    {startDate} ~ {endDate}
                </Card.Text>
            </Card.Body>
        </Card>
    );
};

export default EventCard;
