import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Table, Container, Alert, Spinner } from 'react-bootstrap';
import {
    LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
    PieChart, Pie, Cell, BarChart, Bar
} from 'recharts';
import api from '../../services/api';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8'];

const Dashboard = () => {
    const [visitorTrend, setVisitorTrend] = useState([]);
    const [categoryStats, setCategoryStats] = useState([]);
    const [hashtagStats, setHashtagStats] = useState([]);
    const [topViewed, setTopViewed] = useState([]);
    const [topRated, setTopRated] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [trendRes, catRes, hashRes, viewsRes, ratedRes] = await Promise.all([
                    api.get('/dashboard/visitor-trend?days=7'),
                    api.get('/dashboard/category-stats'),
                    api.get('/dashboard/hashtag-stats'),
                    api.get('/dashboard/top-views'),
                    api.get('/dashboard/top-ratings'),
                ]);

                setVisitorTrend(trendRes.data);
                setCategoryStats(catRes.data);
                setHashtagStats(hashRes.data);
                setTopViewed(viewsRes.data);
                setTopRated(ratedRes.data);
                setLoading(false);
            } catch (err) {
                console.error('Failed to fetch dashboard data:', err);
                setError('데이터를 불러오는 중 오류가 발생했습니다.');
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, []);

    if (loading) return (
        <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
            <Spinner animation="border" variant="primary" />
        </Container>
    );

    if (error) return (
        <Container className="mt-5">
            <Alert variant="danger">{error}</Alert>
        </Container>
    );

    return (
        <Container className="py-4">
            <h2 className="mb-4">대시보드</h2>

            {/* Row 1: Charts */}
            <Row className="mb-4">
                {/* Visitor Trend */}
                <Col lg={8} className="mb-4">
                    <Card className="h-100 shadow-sm">
                        <Card.Body>
                            <Card.Title>주간 방문자 추이</Card.Title>
                            <div style={{ width: '100%', height: 300 }}>
                                <ResponsiveContainer>
                                    <LineChart data={visitorTrend}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="date" />
                                        <YAxis yAxisId="left" />
                                        <YAxis yAxisId="right" orientation="right" />
                                        <Tooltip />
                                        <Legend />
                                        <Line yAxisId="left" type="monotone" dataKey="pageViews" name="페이지 뷰" stroke="#8884d8" activeDot={{ r: 8 }} />
                                        <Line yAxisId="right" type="monotone" dataKey="visitorCount" name="방문자 수" stroke="#82ca9d" />
                                    </LineChart>
                                </ResponsiveContainer>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>

                {/* Category Stats */}
                <Col lg={4} className="mb-4">
                    <Card className="h-100 shadow-sm">
                        <Card.Body>
                            <Card.Title>카테고리별 컨텐츠</Card.Title>
                            <div style={{ width: '100%', height: 300 }}>
                                <ResponsiveContainer>
                                    <PieChart>
                                        <Pie
                                            data={categoryStats}
                                            cx="50%"
                                            cy="50%"
                                            outerRadius={80}
                                            fill="#8884d8"
                                            dataKey="count"
                                            nameKey="categoryName"
                                            label
                                        >
                                            {categoryStats.map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                                            ))}
                                        </Pie>
                                        <Tooltip />
                                        <Legend />
                                    </PieChart>
                                </ResponsiveContainer>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* Row 2: Hashtags & Lists */}
            <Row>
                {/* Hashtag Stats */}
                <Col md={12} lg={4} className="mb-4">
                    <Card className="shadow-sm">
                        <Card.Body>
                            <Card.Title>인기 해시태그 TOP 10</Card.Title>
                            <div style={{ width: '100%', height: 300 }}>
                                <ResponsiveContainer>
                                    <BarChart data={hashtagStats} layout="vertical">
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis type="number" />
                                        <YAxis dataKey="hashtag" type="category" width={100} />
                                        <Tooltip />
                                        <Bar dataKey="count" name="사용 횟수" fill="#ffc658" />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>

                {/* Top Viewed */}
                <Col md={12} lg={4} className="mb-4">
                    <Card className="shadow-sm">
                        <Card.Body>
                            <Card.Title>최다 조회 컨텐츠</Card.Title>
                            <Table hover size="sm" responsive>
                                <thead>
                                    <tr>
                                        <th>제목</th>
                                        <th className="text-end">조회수</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {topViewed.map(content => (
                                        <tr key={content.contentId}>
                                            <td className="text-truncate" style={{ maxWidth: '200px' }}>{content.title}</td>
                                            <td className="text-end">{content.viewCount.toLocaleString()}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </Card.Body>
                    </Card>
                </Col>

                {/* Top Rated */}
                <Col md={12} lg={4} className="mb-4">
                    <Card className="shadow-sm">
                        <Card.Body>
                            <Card.Title>최고 평점 컨텐츠</Card.Title>
                            <Table hover size="sm" responsive>
                                <thead>
                                    <tr>
                                        <th>제목</th>
                                        <th className="text-end">평점</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {topRated.map(content => (
                                        <tr key={content.contentId}>
                                            <td className="text-truncate" style={{ maxWidth: '200px' }}>{content.title}</td>
                                            <td className="text-end">⭐ {content.averageRating.toFixed(1)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default Dashboard;
