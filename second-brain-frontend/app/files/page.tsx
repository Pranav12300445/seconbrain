'use client';

import { useState, useEffect, useRef } from 'react';
import { DashboardLayout } from '@/components/dashboard-layout';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { apiClient } from '@/lib/api-client';
import { useAuth } from '@/lib/auth-context';
import { toast } from 'sonner';
import { Upload, Trash2, File, Search, Download } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { Input } from '@/components/ui/input';

interface FileData {
  id: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  noteId?: string;
  createdAt: string;
  uploadedAt?: string;
}

export default function FilesPage() {
  const [files, setFiles] = useState<FileData[]>([]);
  const [filteredFiles, setFilteredFiles] = useState<FileData[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUploading, setIsUploading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const fileInputRef = useRef<HTMLInputElement>(null);
  const { isAuthenticated } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated) {
      router.push('/login');
      return;
    }
    loadFiles();
  }, [isAuthenticated, router]);

  useEffect(() => {
    const filtered = files.filter((file) =>
      file.fileName.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredFiles(filtered);
  }, [files, searchQuery]);

  const loadFiles = async () => {
    try {
      const response = await apiClient.getFiles();
      setFiles(response.data.data || response.data || []);
    } catch (error: any) {
      console.log('[v0] Error loading files:', error);
      toast.error('Failed to load files');
    } finally {
      setIsLoading(false);
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = e.target.files;
    if (selectedFiles && selectedFiles.length > 0) {
      handleUploadFile(selectedFiles[0]);
    }
  };

  const handleUploadFile = async (file: File) => {
    setIsUploading(true);
    try {
      // In a real app, you would select a note or use a default note
      // For now, we'll upload without a specific note
      const formData = new FormData();
      formData.append('file', file);

      await apiClient.uploadFile('default', file);
      toast.success(`File "${file.name}" uploaded successfully`);
      loadFiles();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to upload file');
      console.log('[v0] Error uploading file:', error);
    } finally {
      setIsUploading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const handleDeleteFile = async (fileId: string) => {
    if (confirm('Are you sure you want to delete this file?')) {
      try {
        await apiClient.deleteFile(fileId);
        toast.success('File deleted successfully');
        loadFiles();
      } catch (error: any) {
        toast.error(error.response?.data?.message || 'Failed to delete file');
        console.log('[v0] Error deleting file:', error);
      }
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
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

  const getFileIcon = (fileType: string) => {
    if (fileType.startsWith('image/')) {
      return '🖼️';
    } else if (fileType.includes('pdf')) {
      return '📄';
    } else if (fileType.includes('word') || fileType.includes('document')) {
      return '📝';
    } else if (fileType.includes('sheet') || fileType.includes('excel')) {
      return '📊';
    } else {
      return '📦';
    }
  };

  return (
    <DashboardLayout>
      <div className="p-8 h-full flex flex-col">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-white mb-2">Your Files</h1>
          <p className="text-gray-400">Manage and organize your uploaded files</p>
        </div>

        <div className="flex gap-4 mb-8">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-500" />
            <Input
              type="text"
              placeholder="Search files..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10 border-slate-600 bg-slate-700/50 text-white placeholder:text-gray-500"
            />
          </div>
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileSelect}
            className="hidden"
            disabled={isUploading}
          />
          <Button
            onClick={() => fileInputRef.current?.click()}
            disabled={isUploading}
            className="bg-cyan-500 hover:bg-cyan-600 text-white"
          >
            <Upload className="w-4 h-4 mr-2" />
            {isUploading ? 'Uploading...' : 'Upload File'}
          </Button>
        </div>

        {/* Files Grid */}
        <div className="flex-1 overflow-y-auto">
          {isLoading ? (
            <div className="flex items-center justify-center py-12">
              <p className="text-gray-400">Loading files...</p>
            </div>
          ) : filteredFiles.length === 0 ? (
            <Card className="border-slate-700 bg-slate-800/50">
              <CardContent className="flex flex-col items-center justify-center py-12">
                <File className="w-12 h-12 text-gray-500 mb-4" />
                <p className="text-gray-400">No files yet</p>
                <p className="text-sm text-gray-500">Upload your first file to get started</p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredFiles.map((file) => (
                <Card key={file.id} className="border-slate-700 bg-slate-800/50 hover:bg-slate-800 transition">
                  <CardHeader>
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="text-3xl mb-2">{getFileIcon(file.fileType)}</div>
                        <CardTitle className="text-lg text-white truncate">{file.fileName}</CardTitle>
                        <CardDescription className="mt-1">{formatFileSize(file.fileSize)}</CardDescription>
                      </div>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <p className="text-xs text-gray-500 mb-4">
                      Uploaded: {formatDate(file.createdAt || file.uploadedAt || '')}
                    </p>
                    <div className="flex gap-2">
                      <Button
                        size="sm"
                        variant="outline"
                        className="flex-1 border-slate-600 hover:bg-slate-700"
                      >
                        <Download className="w-4 h-4 mr-1" />
                        Download
                      </Button>
                      <Button
                        size="sm"
                        variant="destructive"
                        onClick={() => handleDeleteFile(file.id)}
                        className="bg-red-500/20 text-red-400 hover:bg-red-500/30 border-red-500/30"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          )}
        </div>
      </div>
    </DashboardLayout>
  );
}
