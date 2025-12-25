import React from 'react';
import { Card, Badge } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';

const ContentCard = ({ content }) => {
  const navigate = useNavigate();

  const handleCardClick = () => {
    navigate(`/magazine/${content.contentId}`);
  };

  // Format date
  const formattedDate = new Date(content.publishedAt).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  return (
    <Card className="h-100 shadow-sm cursor-pointer" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
      {/* Thumbnail Placeholder logic if URL is empty */}
      <div style={{ height: '200px', overflow: 'hidden', backgroundColor: '#f0f0f0' }}>
        {content.thumbnailUrl ? (
          <Card.Img 
            variant="top" 
            src={content.thumbnailUrl} 
            alt={content.title}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
          />
        ) : (
          <div className="d-flex align-items-center justify-content-center h-100 text-muted">
            No Image
          </div>
        )}
      </div>
      
      <Card.Body className="d-flex flex-column">
        <div className="mb-2">
          <Badge bg="primary" className="me-2">{content.categoryName}</Badge>
        </div>
        
        <Card.Title className="text-truncate" title={content.title}>
          {content.title}
        </Card.Title>
        
        <Card.Text className="flex-grow-1 text-muted" style={{ 
          display: '-webkit-box',
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden'
        }}>
          {content.summary}
        </Card.Text>

        <div className="mt-3">
          {content.hashtags && content.hashtags.slice(0, 3).map((tag, index) => (
             <span key={index} className="text-primary me-2 small">#{tag}</span>
          ))}
        </div>
      </Card.Body>
      
      <Card.Footer className="bg-white text-muted small d-flex justify-content-between">
        <span>{formattedDate}</span>
        <span>
            views {content.viewCount} · ⭐ {content.averageRating ? content.averageRating.toFixed(1) : '0.0'}
        </span>
      </Card.Footer>
    </Card>
  );
};

export default ContentCard;
