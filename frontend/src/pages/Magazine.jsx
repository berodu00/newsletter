import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Spinner, Pagination, Alert } from 'react-bootstrap';
import api from '../services/api';
import ContentCard from '../components/magazine/ContentCard';

const Magazine = () => {
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Pagination state
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const size = 12; // Items per page

    useEffect(() => {
        const fetchContents = async () => {
            try {
                setLoading(true);
                // Default sort: publishedAt,desc
                const response = await api.get(`/contents?page=${page}&size=${size}&sort=publishedAt,desc`);
                setContents(response.data.content);
                setTotalPages(response.data.totalPages);
                setError(null);
            } catch (err) {
                console.error('Failed to fetch contents', err);
                setError('사보 목록을 불러오는데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchContents();
    }, [page]);

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setPage(newPage);
            window.scrollTo(0, 0);
        }
    };

    if (loading && contents.length === 0) {
        return (
            <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            </Container>
        );
    }

    return (
        <Container className="py-5">
            <h2 className="mb-4 fw-bold">Magazine</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            <Row xs={1} md={2} lg={3} xl={4} className="g-4 mb-5">
                {contents.map(content => (
                    <Col key={content.contentId}>
                        <ContentCard content={content} />
                    </Col>
                ))}
            </Row>

            {contents.length === 0 && !loading && (
                <div className="text-center py-5 text-muted">
                    등록된 사보가 없습니다.
                </div>
            )}

            {totalPages > 1 && (
                <Pagination className="justify-content-center">
                    <Pagination.First onClick={() => handlePageChange(0)} disabled={page === 0} />
                    <Pagination.Prev onClick={() => handlePageChange(page - 1)} disabled={page === 0} />

                    {[...Array(totalPages)].map((_, idx) => (
                        <Pagination.Item
                            key={idx}
                            active={idx === page}
                            onClick={() => handlePageChange(idx)}
                        >
                            {idx + 1}
                        </Pagination.Item>
                    ))}

                    <Pagination.Next onClick={() => handlePageChange(page + 1)} disabled={page === totalPages - 1} />
                    <Pagination.Last onClick={() => handlePageChange(totalPages - 1)} disabled={page === totalPages - 1} />
                </Pagination>
            )}
        </Container>
    );
};

export default Magazine;
