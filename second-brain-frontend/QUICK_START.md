# Second Brain Frontend - Quick Start

## What's Included

A complete Next.js frontend for your Second Brain backend with:

### Authentication Pages
- **Login Page** (`/login`) - Secure user login with JWT
- **Register Page** (`/register`) - User registration
- **Auto Token Refresh** - Handles token expiration automatically

### Main Features
1. **Dashboard** (`/dashboard`) - Grid view of all notes with create, edit, delete actions
2. **Notes Page** (`/notes`) - Detailed note editor with search functionality
3. **Files Page** (`/files`) - File management with upload capability
4. **Sidebar Navigation** - Easy access to all features with collapsible menu

### Technical Stack
- Next.js 15 with App Router
- React 19
- TypeScript
- Tailwind CSS with dark theme
- shadcn/ui components
- Axios for API calls
- Sonner for notifications

## Getting Started (3 Steps)

### Step 1: Environment Setup
```bash
# Copy environment template
cp .env.example .env.local

# Edit .env.local and set your backend URL
# NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Step 2: Start Development Server
```bash
pnpm dev
```

The app will be running at `http://localhost:3000`

### Step 3: Log In
- Create a new account or log in with existing credentials
- Your backend must be running for authentication to work

## File Structure Quick Reference

```
app/
├── login/              # Login page
├── register/           # Registration page
├── dashboard/          # Main dashboard
├── notes/              # Notes list & editor
├── files/              # File manager
└── layout.tsx          # Root layout with AuthProvider

lib/
├── api-client.ts       # Axios API client with JWT handling
└── auth-context.tsx    # React Context for authentication

components/
└── dashboard-layout.tsx # Sidebar + main layout
```

## Key Features Explained

### 1. Authentication Flow
- Register new account → Get JWT token → Redirected to dashboard
- JWT stored in cookies + localStorage for persistence
- Auto-refresh on 401 errors
- Logout clears all auth data

### 2. Notes Management
- **Create**: Click "New Note" button, fill title & content
- **Read**: View all notes in grid or list format
- **Update**: Click edit button to modify existing note
- **Delete**: Remove notes with confirmation

### 3. File Management
- Upload files to associate with your notes
- View all uploaded files with file type icons
- Delete files you no longer need
- File size and upload date tracking

### 4. Dark Theme
- Modern dark interface with cyan accents
- Responsive design works on mobile and desktop
- Smooth transitions and hover effects

## API Integration

The frontend connects to your backend endpoints:

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/auth/register` | Create account |
| POST | `/auth/login` | Sign in |
| POST | `/auth/refresh` | Refresh token |
| POST | `/auth/logout` | Sign out |
| POST | `/notes` | Create note |
| GET | `/notes` | Get all notes |
| PUT | `/notes/{id}` | Update note |
| DELETE | `/notes/{id}` | Delete note |
| POST | `/files/upload` | Upload file |
| GET | `/files` | Get all files |
| DELETE | `/files/{id}` | Delete file |

## Configuration

### Backend URL
Update `NEXT_PUBLIC_API_URL` in `.env.local` to point to your backend:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Theme Colors
Dark theme with cyan accents is configured in `app/globals.css`:
- Background: Deep slate (#0f172a)
- Accent: Cyan (#22d3ee)
- Text: Light gray (#e2e8f0)

## Common Issues & Solutions

### "Failed to fetch from API"
- Check backend is running on correct port
- Verify `NEXT_PUBLIC_API_URL` in `.env.local`
- Check browser console for CORS errors

### "Unauthorized" errors
- Login and get a new token
- Check that backend returns valid JWT
- Try clearing browser cookies/localStorage

### Styling looks off
- Ensure Tailwind CSS is compiled (should be automatic)
- Clear browser cache
- Restart dev server: `Ctrl+C` then `pnpm dev`

## Next Steps

1. **Customize Branding**: Update colors and logo in components
2. **Add Features**: Create new pages following existing patterns
3. **Connect to Backend**: Update API endpoints if they differ
4. **Deploy**: Use `pnpm build && pnpm start` for production
5. **Styling**: Adjust colors in `app/globals.css` CSS variables

## Support

- Check `SETUP.md` for detailed documentation
- Review component files for implementation details
- API client is well-commented in `lib/api-client.ts`
- Auth context logic in `lib/auth-context.tsx`

## Production Checklist

- [ ] Backend deployed and accessible
- [ ] `.env.local` configured with production API URL
- [ ] CORS enabled on backend for frontend URL
- [ ] JWT secret configured consistently
- [ ] Run `pnpm build` to check for errors
- [ ] Test login/register flow
- [ ] Test note creation and file upload
- [ ] Deploy to Vercel or your hosting platform

## Deployment to Vercel

```bash
# Push to GitHub first
git add .
git commit -m "Initial commit"
git push

# Then connect to Vercel
# vercel.com → New Project → Select repo → Deploy
```

Configure environment variables in Vercel project settings:
```
NEXT_PUBLIC_API_URL=https://your-backend.com/api
```

Enjoy building with Second Brain!
