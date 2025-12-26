import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Layout from './components/common/Layout';
import Home from './pages/Home';
import Login from './pages/Login';
import Magazine from './pages/Magazine';
import ContentDetail from './pages/ContentDetail';
import ContentManagement from './pages/admin/ContentManagement';
import ContentForm from './pages/admin/ContentForm';
import EventManagement from './pages/admin/EventManagement';
import EventForm from './pages/admin/EventForm';
import 'bootstrap/dist/css/bootstrap.min.css';

import { AuthProvider } from './contexts/AuthContext';

import PrivateRoute from './components/common/PrivateRoute';

import Social from './pages/Social';
import SocialEmbed from './pages/SocialEmbed';

import Events from './pages/Events';
import EventDetail from './pages/EventDetail';

function App() {
  return (
    <AuthProvider>
      <Router>
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
            <Route path="ideas" element={<div>Ideas</div>} />
          </Route>

          import EventManagement from './pages/admin/EventManagement';
          import EventForm from './pages/admin/EventForm';

          // ... (existing code)

          {/* Admin Routes */}
          <Route element={<PrivateRoute roles={['ADMIN']} />}>
            <Route path="/admin" element={<Layout />}>
              <Route index element={<div>Admin Dashboard</div>} />
              <Route path="contents" element={<ContentManagement />} />
              <Route path="contents/new" element={<ContentForm />} />
              <Route path="contents/:id/edit" element={<ContentForm />} />
              <Route path="events" element={<EventManagement />} />
              <Route path="events/new" element={<EventForm />} />
              <Route path="events/:id/edit" element={<EventForm />} />
            </Route>
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
