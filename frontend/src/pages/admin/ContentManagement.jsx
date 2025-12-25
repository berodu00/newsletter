import React, { useState, useEffect } from 'react';
import { Container, Table, Button, Badge, Form, Pagination, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';

const ContentManagement = () => {
    const navigate = useNavigate();
    const [contents, setContents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Filters and Pagination
    const [status, setStatus] = useState('PUBLISHED');
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const size = 10;

    const fetchContents = async () => {
        try {
            setLoading(true);
            const response = await api.get(`/contents?page=${page}&size=${size}&status=${status}&sort=contentId,desc`);
            setContents(response.data.content);
            setTotalPages(response.data.totalPages);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch contents', err);
            setError('컨텐츠 목록을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchContents();
    }, [page, status]);

    const handleDelete = async (contentId) => {
        if (window.confirm('정말 삭제하시겠습니까?')) {
            try {
                await api.delete(`/contents/${contentId}`);
                fetchContents(); // Refresh list
            } catch (err) {
                console.error('Delete failed', err);
                alert('삭제에 실패했습니다.');
            }
        }
    };

    const getStatusBadge = (status) => {
        switch (status) {
            case 'PUBLISHED': return 'success';
            case 'DRAFT': return 'secondary';
            case 'SCHEDULED': return 'info';
            case 'ARCHIVED': return 'warning';
            default: return 'light';
        }
    };

    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2 className="fw-bold">컨텐츠 관리</h2>
                <Button variant="primary" onClick={() => navigate('/admin/contents/new')}>
                    새 컨텐츠 작성
                </Button>
            </div>

            <div className="mb-4">
                <Form.Select
                    value={status}
                    onChange={(e) => { setStatus(e.target.value); setPage(0); }}
                    style={{ width: '200px' }}
                >
                    <option value="PUBLISHED">발행됨 (PUBLISHED)</option>
                    <option value="DRAFT">임시저장 (DRAFT)</option>
                    <option value="SCHEDULED">예약됨 (SCHEDULED)</option>
                    <option value="ARCHIVED">보관됨 (ARCHIVED)</option>
                </Form.Select>
            </div>

            {error && <Alert variant="danger">{error}</Alert>}

            <Table hover responsive className="align-middle">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>제목</th>
                        <th>상태</th>
                        <th>카테고리</th>
                        <th>조회수</th>
                        <th>작성일</th>
                        <th>관리</th>
                    </tr>
                </thead>
                <tbody>
                    {contents.map(content => (
                        <tr key={content.contentId}>
                            <td>{content.contentId}</td>
                            <td style={{ maxWidth: '300px' }} className="text-truncate">
                                {content.title}
                            </td>
                            <td><Badge bg={getStatusBadge(content.status)}>{content.status}</Badge></td>
                            <td>{content.categoryName}</td>
                            <td>{content.viewCount}</td>
                            <td>{new Date(content.publishedAt || content.createdAt).toLocaleDateString()}</td>
                            <td>
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    className="me-2"
                                    onClick={() => navigate(`/admin/contents/${content.contentId}/edit`)}
                                >
                                    수정
                                </Button>
                                <Button
                                    variant="outline-danger"
                                    size="sm"
                                    onClick={() => handleDelete(content.contentId)}
                                >
                                    삭제
                                </Button>
                            </td>
                        </tr>
                    ))}
                    {contents.length === 0 && !loading && (
                        <tr>
                            <td colSpan="7" className="text-center py-4 text-muted">컨텐츠가 없습니다.</td>
                        </tr>
                    )}
                </tbody>
            </Table>

            {/* Pagination (Common Logic - reusable?) */}
            {totalPages > 1 && (
                <Pagination className="justify-content-center mt-4">
                    <Pagination.Prev onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} />
                    <Pagination.Item active>{page + 1}</Pagination.Item>
                    <Pagination.Next onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page === totalPages - 1} />
                </Pagination>
            )}
        </Container>
    );
};

export default ContentManagement;
