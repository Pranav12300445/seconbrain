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
import { Plus, Trash2, Edit2, FileText, Search, ChevronRight } from 'lucide-react';
import { useRouter } from 'next/navigation';

interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export default function NotesPage() {
  const [notes, setNotes] = useState<Note[]>([]);
  const [filteredNotes, setFilteredNotes] = useState<Note[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedNote, setSelectedNote] = useState<Note | null>(null);
  const { isAuthenticated } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    loadNotes();
  }, [isAuthenticated, router]);

  useEffect(() => {
    const filtered = notes.filter(
      (note) =>
        note.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        note.content.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredNotes(filtered);
  }, [notes, searchQuery]);

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
        setSelectedNote(null);
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
        setSelectedNote(null);
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
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateString;
    }
  };

  return (
    <DashboardLayout>
      <div className="p-8 h-full flex flex-col">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">My Notes</h1>
          <p className="text-gray-400">All your saved notes in one place</p>
        </div>

        <div className="flex gap-4 mb-8">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500" />
            <Input
              type="text"
              placeholder="Search notes..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 border-slate-600 bg-slate-700/50 text-white placeholder:text-gray-500"
            />
          </div>
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
            <DialogContent className="bg-slate-800 border-slate-700 text-white max-w-2xl">
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
                    className="mt-1 border-slate-600 bg-slate-700/50 text-white placeholder:text-gray-500 min-h-[300px]"
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

        {/* Content Area */}
        <div className="flex-1 flex gap-6 overflow-hidden">
          {/* Notes List */}
          <div className="w-full md:w-80 border-r border-slate-700 overflow-y-auto">
            {isLoading ? (
              <div className="flex items-center justify-center py-12">
                <p className="text-gray-400">Loading notes...</p>
              </div>
            ) : filteredNotes.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-12 px-4">
                <FileText className="w-8 h-8 text-gray-500 mb-2" />
                <p className="text-gray-400 text-sm">No notes found</p>
              </div>
            ) : (
              <div className="space-y-2 pr-4">
                {filteredNotes.map((note) => (
                  <button
                    key={note.id}
                    onClick={() => setSelectedNote(note)}
                    className={`w-full text-left p-3 rounded-lg transition ${
                      selectedNote?.id === note.id
                        ? 'bg-cyan-500/20 border border-cyan-500/50'
                        : 'bg-slate-800/50 border border-slate-700 hover:bg-slate-700/50'
                    }`}
                  >
                    <h3 className="font-medium text-white truncate">{note.title}</h3>
                    <p className="text-xs text-gray-500 mt-1">{formatDate(note.updatedAt)}</p>
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Note Detail */}
          {selectedNote ? (
            <div className="flex-1 flex flex-col overflow-hidden">
              <div className="border-b border-slate-700 pb-4 mb-4">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <h2 className="text-2xl font-bold text-white">{selectedNote.title}</h2>
                    <p className="text-sm text-gray-500 mt-2">{formatDate(selectedNote.updatedAt)}</p>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => handleEditNote(selectedNote)}
                      className="border-slate-600 hover:bg-slate-700"
                    >
                      <Edit2 className="w-4 h-4 mr-1" />
                      Edit
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => handleDeleteNote(selectedNote.id)}
                      className="bg-red-500/20 text-red-400 hover:bg-red-500/30 border-red-500/30"
                    >
                      <Trash2 className="w-4 h-4 mr-1" />
                      Delete
                    </Button>
                  </div>
                </div>
              </div>
              <div className="flex-1 overflow-y-auto">
                <div className="prose prose-invert max-w-none">
                  <p className="text-gray-300 whitespace-pre-wrap">{selectedNote.content}</p>
                </div>
              </div>
            </div>
          ) : (
            <div className="flex-1 flex items-center justify-center">
              <div className="text-center">
                <FileText className="w-16 h-16 text-gray-600 mx-auto mb-4" />
                <p className="text-gray-400">Select a note to view</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
}
