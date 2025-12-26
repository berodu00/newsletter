import React, { Suspense, lazy } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/common/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Magazine from './pages/Magazine';
import ContentDetail from './pages/ContentDetail';
import 'bootstrap/dist/css/bootstrap.min.css';

import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/common/PrivateRoute';
import { Spinner } from 'react-bootstrap';

// Lazy load Code-Splitting
const Social = lazy(() => import('./pages/Social'));
const SocialEmbed = lazy(() => import('./pages/SocialEmbed'));
const Events = lazy(() => import('./pages/Events'));
const EventDetail = lazy(() => import('./pages/EventDetail'));
const Ideas = lazy(() => import('./pages/Ideas'));

// Admin Pages Lazy Loading
const ContentManagement = lazy(() => import('./pages/admin/ContentManagement'));
const ContentForm = lazy(() => import('./pages/admin/ContentForm'));
const EventManagement = lazy(() => import('./pages/admin/EventManagement'));
const EventForm = lazy(() => import('./pages/admin/EventForm'));
const IdeaManagement = lazy(() => import('./pages/admin/IdeaManagement'));
const PopupManagement = lazy(() => import('./pages/admin/PopupManagement'));
const Dashboard = lazy(() => import('./pages/admin/Dashboard'));

const LoadingFallback = () => (
  <div className="d-flex justify-content-center align-items-center vh-100">
    <Spinner animation="border" variant="primary" />
  </div>
);

function App() {
  return (
    <AuthProvider>
      <Router>
        <Suspense fallback={<LoadingFallback />}>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/embed/social" element={<SocialEmbed />} />

            <Route path="/" element={<Layout />}>
              <Route index element={<Home />} />
              <Route path="magazine" element={<Magazine />} />
              <Route path="magazine/:id" element={<ContentDetail />} />
              <Route path="social" element={<Social />} />
              <Route path="events" element={<Events />} />
              <Route path="events/:id" element={<EventDetail />} />
              <Route path="ideas" element={<Ideas />} />
            </Route>

            {/* Admin Routes */}
            <Route element={<PrivateRoute roles={['ADMIN']} />}>
              <Route path="/admin" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="contents" element={<ContentManagement />} />
                <Route path="contents/new" element={<ContentForm />} />
                <Route path="contents/:id/edit" element={<ContentForm />} />
                <Route path="events" element={<EventManagement />} />
                <Route path="events/new" element={<EventForm />} />
                <Route path="events/:id/edit" element={<EventForm />} />
                <Route path="ideas" element={<IdeaManagement />} />
                <Route path="popups" element={<PopupManagement />} />
              </Route>
            </Route>
          </Routes>
        </Suspense>
      </Router>
    </AuthProvider>
  );
}

export default App;
