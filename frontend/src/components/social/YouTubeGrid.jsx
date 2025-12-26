import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Spinner, Alert, Modal, Ratio } from 'react-bootstrap';
import ContentCard from '../magazine/ContentCard';
import api from '../../services/api';

const YouTubeGrid = () => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [selectedVideoUrl, setSelectedVideoUrl] = useState('');

    useEffect(() => {
        const fetchContents = async () => {
            try {
                setLoading(true);
                // Fetch contents that have YouTube URL
                const response = await api.get('/contents', {
                    params: { hasYoutubeUrl: true, size: 12, sort: 'publishedAt,desc' }
                });
                setContents(response.data.content);
            } catch (err) {
                console.error('Failed to fetch YouTube contents', err);
                setError('유튜브 컨텐츠를 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchContents();
    }, []);

    const handleCardClick = (content) => {
        if (content.youtubeUrl) {
            // Extract video ID or use embed URL directly if implementation allowed
            // Assuming youtubeUrl is a standard link like https://www.youtube.com/watch?v=VIDEO_ID
            // We need to convert to embed URL https://www.youtube.com/embed/VIDEO_ID
            const videoId = extractVideoId(content.youtubeUrl);
            if (videoId) {
                setSelectedVideoUrl(`https://www.youtube.com/embed/${videoId}?autoplay=1`);
                setShowModal(true);
            } else {
                window.open(content.youtubeUrl, '_blank');
            }
        }
    };

    const extractVideoId = (url) => {
        const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
        const match = url.match(regExp);
        return (match && match[2].length === 11) ? match[2] : null;
    };

    const handleClose = () => {
        setShowModal(false);
        setSelectedVideoUrl('');
    };

    if (loading) {
        return (
            <div className="text-center py-5">
                <Spinner animation="border" variant="danger" />
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
                    등록된 유튜브 영상이 없습니다.
                </div>
            )}

            <Modal show={showModal} onHide={handleClose} size="lg" centered>
                <Modal.Body className="p-0 bg-black">
                    <Ratio aspectRatio="16x9">
                        <iframe
                            src={selectedVideoUrl}
                            title="YouTube video"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                        ></iframe>
                    </Ratio>
                </Modal.Body>
            </Modal>
        </Container>
    );
};

export default YouTubeGrid;
