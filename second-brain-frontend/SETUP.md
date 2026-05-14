# Second Brain Frontend - Setup Guide

## Overview

This is a modern, full-featured frontend for the Second Brain backend application built with Next.js 15, React, and shadcn/ui components.

## Features

- **Authentication System**: Secure login/register with JWT tokens
- **Notes Management**: Create, read, update, and delete notes
- **File Management**: Upload, view, and manage files
- **Dashboard**: Beautiful, intuitive interface with sidebar navigation
- **Dark Theme**: Modern dark mode with cyan accents for a professional look
- **Responsive Design**: Works seamlessly on desktop and mobile devices
- **Auto Token Refresh**: Automatic JWT token refresh on 401 errors

## Project Structure

```
├── app/
│   ├── layout.tsx          # Root layout with Auth provider
│   ├── page.tsx            # Home page (redirects to dashboard/login)
│   ├── login/
│   │   └── page.tsx        # Login page
│   ├── register/
│   │   └── page.tsx        # Register page
│   ├── dashboard/
│   │   └── page.tsx        # Main dashboard with notes grid
│   ├── notes/
│   │   └── page.tsx        # Detailed notes list and editor
│   ├── files/
│   │   └── page.tsx        # File management page
│   └── globals.css         # Global styles and theme tokens
├── lib/
│   ├── api-client.ts       # Axios API client with interceptors
│   └── auth-context.tsx    # React Context for auth state
├── components/
│   └── dashboard-layout.tsx # Sidebar layout component
└── .env.example            # Environment variables template
```

## Getting Started

### 1. Install Dependencies

```bash
pnpm install
```

### 2. Configure Environment Variables

Create a `.env.local` file based on `.env.example`:

```bash
cp .env.example .env.local
```

Update the API URL in `.env.local`:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

Adjust the URL to match your backend's location.

### 3. Start the Development Server

```bash
pnpm dev
```

The application will be available at `http://localhost:3000`

## API Endpoints

The frontend integrates with these backend endpoints:

### Authentication
- `POST /auth/register` - Create new account
- `POST /auth/login` - Login with email/password
- `POST /auth/refresh` - Refresh JWT token
- `POST /auth/logout` - Logout

### Notes
- `POST /notes` - Create a new note
- `GET /notes` - Get all user notes
- `PUT /notes/{noteId}` - Update a note
- `DELETE /notes/{noteId}` - Delete a note

### Files
- `POST /files/upload` - Upload a file
- `GET /files` - Get all files
- `GET /files/notes/{noteId}` - Get files for a specific note
- `DELETE /files/{fileId}` - Delete a file

## Authentication Flow

The app implements a secure JWT authentication system:

1. **Registration/Login**: User credentials are sent to backend, JWT token is received
2. **Token Storage**: JWT is stored in cookies and localStorage for persistence
3. **Auto-Refresh**: Token is automatically refreshed on 401 responses
4. **Protected Routes**: Routes redirect to login if not authenticated
5. **Logout**: Clears token and user data on logout

## UI Components

The application uses shadcn/ui components:
- `Button` - Interactive buttons
- `Card` - Content cards with headers
- `Input` - Text input fields
- `Textarea` - Multi-line text input
- `Dialog` - Modal dialogs
- `Separator` - Visual dividers
- Custom dark theme styling with cyan accents

## Styling

- **Framework**: Tailwind CSS with dark theme
- **Color Scheme**: 
  - Primary Dark: `#0f172a` (slate-900)
  - Secondary: `#1e293b` (slate-800)
  - Accent: Cyan (`#22d3ee`)
- **Typography**: Geist font family

## Development Tips

### Adding New Pages
1. Create a new directory in `app/`
2. Add `page.tsx` file
3. Wrap with `DashboardLayout` for authenticated pages
4. Use `useAuth()` hook to check authentication

### Making API Calls
```typescript
import { apiClient } from '@/lib/api-client';

// Example
const response = await apiClient.createNote('Title', 'Content');
```

### Displaying Notifications
```typescript
import { toast } from 'sonner';

toast.success('Operation successful!');
toast.error('Something went wrong');
```

## Troubleshooting

### "API call failed" errors
- Check that backend is running on correct port
- Verify `NEXT_PUBLIC_API_URL` in `.env.local`
- Check browser console for CORS issues

### "Not authenticated" redirects
- Ensure backend is returning valid JWT tokens
- Check that tokens are being stored correctly in browser
- Clear browser cookies/localStorage and re-login

### File upload not working
- Verify backend accepts multipart/form-data
- Check file size limits on backend
- Ensure file upload endpoint is implemented

## Building for Production

```bash
pnpm build
pnpm start
```

## Notes on UserController and AdminController

These controllers are in testing phase and are not integrated into the frontend. When they are ready, the following features can be added:

- User profile management
- User settings and preferences
- Admin dashboard for user management
- Admin analytics and reporting

## Support

For issues or questions about the frontend implementation, refer to the component files and comments. The API client is well-documented with inline comments.
