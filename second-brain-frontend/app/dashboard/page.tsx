'use client';

import { useState, useEffect } from 'react';
import { DashboardLayout } from '@/components/dashboard-layout';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { apiClient } from '@/lib/api-client';
import { useAuth } from '@/lib/auth-context';
import { toast } from 'sonner';
import { Plus, Trash2, Edit2, FileText, Calendar } from 'lucide-react';
import { useRouter } from 'next/navigation';

interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export default function DashboardPage() {
  const [notes, setNotes] = useState<Note[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const { isAuthenticated } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    loadNotes();
  }, [isAuthenticated, router]);

  const loadNotes = async () => {
    try {
      const response = await apiClient.getNotes();
      setNotes(response.data.data || response.data || []);
    } catch (error: any) {
      console.log('[v0] Error loading notes:', error);
      toast.error('Failed to load notes');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSaveNote = async () => {
    if (!title.trim() || !content.trim()) {
      toast.error('Please fill in all fields');
      return;
    }

    setIsSaving(true);
    try {
      if (editingNote) {
        await apiClient.updateNote(editingNote.id, title, content);
        toast.success('Note updated successfully');
        setEditingNote(null);
      } else {
        await apiClient.createNote(title, content);
        toast.success('Note created successfully');
      }
      setTitle('');
      setContent('');
      setIsDialogOpen(false);
      loadNotes();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to save note');
      console.log('[v0] Error saving note:', error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleEditNote = (note: Note) => {
    setEditingNote(note);
    setTitle(note.title);
    setContent(note.content);
    setIsDialogOpen(true);
  };

  const handleDeleteNote = async (noteId: string) => {
    if (confirm('Are you sure you want to delete this note?')) {
      try {
        await apiClient.deleteNote(noteId);
        toast.success('Note deleted successfully');
        loadNotes();
      } catch (error: any) {
        toast.error(error.response?.data?.message || 'Failed to delete note');
        console.log('[v0] Error deleting note:', error);
      }
    }
  };

  const handleCloseDialog = () => {
    setIsDialogOpen(false);
    setEditingNote(null);
    setTitle('');
    setContent('');
  };

  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
      });
    } catch {
      return dateString;
    }
  };

  return (
    <DashboardLayout>
      <div className="p-8 max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">Your Notes</h1>
          <p className="text-gray-400">Organize your thoughts and ideas</p>
        </div>

        {/* Create Note Button */}
        <div className="mb-8">
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogTrigger asChild>
              <Button
                onClick={() => {
                  setEditingNote(null);
                  setTitle('');
                  setContent('');
                }}
                className="bg-cyan-500 hover:bg-cyan-600 text-white"
              >
                <Plus className="w-4 h-4 mr-2" />
                New Note
              </Button>
            </DialogTrigger>
            <DialogContent className="bg-slate-800 border-slate-700 text-white">
              <DialogHeader>
                <DialogTitle>{editingNote ? 'Edit Note' : 'Create New Note'}</DialogTitle>
                <DialogDescription>
                  {editingNote ? 'Update your note' : 'Add a new note to your collection'}
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div>
                  <label className="text-sm font-medium text-gray-300">Title</label>
                  <Input
                    placeholder="Note title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="mt-1 border-slate-600 bg-slate-700/50 text-white placeholder:text-gray-500"
                  />
                </div>
                <div>
                  <label className="text-sm font-medium text-gray-300">Content</label>
                  <Textarea
                    placeholder="Note content"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    className="mt-1 border-slate-600 bg-slate-700/50 text-white placeholder:text-gray-500 min-h-[200px]"
                  />
                </div>
                <div className="flex gap-3 justify-end">
                  <Button variant="outline" onClick={handleCloseDialog}>
                    Cancel
                  </Button>
                  <Button
                    onClick={handleSaveNote}
                    disabled={isSaving}
                    className="bg-cyan-500 hover:bg-cyan-600 text-white"
                  >
                    {isSaving ? 'Saving...' : 'Save Note'}
                  </Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        {/* Notes Grid */}
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <p className="text-gray-400">Loading notes...</p>
          </div>
        ) : notes.length === 0 ? (
          <Card className="border-slate-700 bg-slate-800/50">
            <CardContent className="flex flex-col items-center justify-center py-12">
              <FileText className="w-12 h-12 text-gray-500 mb-4" />
              <p className="text-gray-400">No notes yet</p>
              <p className="text-sm text-gray-500">Create your first note to get started</p>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {notes.map((note) => (
              <Card key={note.id} className="border-slate-700 bg-slate-800/50 hover:bg-slate-800 transition cursor-pointer">
                <CardHeader>
                  <CardTitle className="text-lg text-white truncate">{note.title}</CardTitle>
                  <CardDescription className="flex items-center gap-2">
                    <Calendar className="w-3 h-3" />
                    {formatDate(note.updatedAt || note.createdAt)}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <p className="text-gray-400 text-sm line-clamp-3 mb-4">{note.content}</p>
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => handleEditNote(note)}
                      className="border-slate-600 hover:bg-slate-700"
                    >
                      <Edit2 className="w-4 h-4 mr-1" />
                      Edit
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => handleDeleteNote(note.id)}
                      className="bg-red-500/20 text-red-400 hover:bg-red-500/30 border-red-500/30"
                    >
                      <Trash2 className="w-4 h-4 mr-1" />
                      Delete
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
