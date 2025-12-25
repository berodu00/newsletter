import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Spinner, Badge, Button, Alert } from 'react-bootstrap';
import api from '../services/api';

const ContentDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [content, setContent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchContent = async () => {
            try {
                setLoading(true);
                const response = await api.get(`/contents/${id}`);
                setContent(response.data);
            } catch (err) {
                console.error('Failed to fetch content detail', err);
                setError('사보 내용을 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchContent();
    }, [id]);

    if (loading) {
        return (
            <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="py-5">
                <Alert variant="danger">{error}</Alert>
                <Button variant="outline-secondary" onClick={() => navigate('/magazine')}>
                    목록으로 돌아가기
                </Button>
            </Container>
        );
    }

    if (!content) return null;

    const formattedDate = new Date(content.publishedAt).toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
    });

    return (
        <Container className="py-5" style={{ maxWidth: '800px' }}>
            {/* Header */}
            <div className="mb-4 text-center">
                <Badge bg="primary" className="mb-2">{content.categoryName}</Badge>
                <h1 className="fw-bold mb-3">{content.title}</h1>
                <div className="text-muted small">
                    <span className="me-3">{content.author.name}</span>
                    <span className="me-3">{formattedDate}</span>
                    <span>조회 {content.viewCount}</span>
                </div>
            </div>

            <hr className="my-4" />

            {/* Body */}
            <div className="content-body" style={{ minHeight: '300px' }}>
                <div dangerouslySetInnerHTML={{ __html: content.bodyHtml }} />
            </div>

            <hr className="my-5" />

            {/* Footer / Meta */}
            <div className="d-flex justify-content-between align-items-center mb-5">
                <div>
                    {content.hashtags && content.hashtags.map(tag => (
                        <span key={tag.hashtagId} className="text-primary me-2">#{tag.hashtagName}</span>
                    ))}
                </div>
                <Button variant="outline-secondary" onClick={() => navigate('/magazine')}>
                    목록
                </Button>
            </div>

            {/* Reactions/Comments Placeholder */}
            <div className="text-center text-muted py-5 bg-light rounded">
                <p className="mb-0">반응/댓글 기능은 준비 중입니다.</p>
            </div>

        </Container>
    );
};

export default ContentDetail;
