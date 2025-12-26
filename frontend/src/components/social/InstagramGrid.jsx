import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Spinner, Alert } from 'react-bootstrap';
import ContentCard from '../magazine/ContentCard';
import api from '../../services/api';

const InstagramGrid = () => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchContents = async () => {
            try {
                setLoading(true);
                // Fetch contents that have Instagram URL
                const response = await api.get('/contents', {
                    params: { hasInstagramUrl: true, size: 12, sort: 'publishedAt,desc' }
                });
                setContents(response.data.content);
            } catch (err) {
                console.error('Failed to fetch Instagram contents', err);
                setError('인스타그램 컨텐츠를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchContents();
    }, []);

    const handleCardClick = (content) => {
        if (content.instagramUrl) {
            window.open(content.instagramUrl, '_blank');
        }
    };

    if (loading) {
        return (
            <div className="text-center py-5">
                <Spinner animation="border" variant="warning" /> {/* Instagram-ish color? */}
            </div>
        );
    }

    if (error) {
        return <Alert variant="danger" className="m-3">{error}</Alert>;
    }

    return (
        <Container fluid className="p-0">
            <Row xs={1} md={2} lg={3} xl={4} className="g-4">
                {contents.map(content => (
                    <Col key={content.contentId}>
                        <ContentCard content={content} onClick={handleCardClick} />
                    </Col>
                ))}
            </Row>
            {contents.length === 0 && (
                <div className="text-center py-5 text-muted">
                    등록된 인스타그램 게시물이 없습니다.
                </div>
            )}
        </Container>
    );
};

export default InstagramGrid;
