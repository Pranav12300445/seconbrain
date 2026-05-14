/**
 * Loading skeleton components for better UX during data fetching
 */

export function NoteSkeleton() {
  return (
    <div className="border border-slate-700 bg-slate-800/50 rounded-lg p-4 space-y-4 animate-pulse">
      <div className="h-6 bg-slate-700 rounded w-3/4"></div>
      <div className="h-4 bg-slate-700 rounded w-full"></div>
      <div className="h-4 bg-slate-700 rounded w-5/6"></div>
      <div className="flex gap-2 pt-2">
        <div className="h-8 bg-slate-700 rounded w-20"></div>
        <div className="h-8 bg-slate-700 rounded w-20"></div>
      </div>
    </div>
  );
}

export function NoteGridSkeleton({ count = 3 }: { count?: number }) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {Array.from({ length: count }).map((_, i) => (
        <NoteSkeleton key={i} />
      ))}
    </div>
  );
}

export function FileSkeleton() {
  return (
    <div className="border border-slate-700 bg-slate-800/50 rounded-lg p-4 space-y-4 animate-pulse">
      <div className="flex items-center gap-4">
        <div className="h-12 w-12 bg-slate-700 rounded"></div>
        <div className="flex-1">
          <div className="h-5 bg-slate-700 rounded w-3/4"></div>
          <div className="h-4 bg-slate-700 rounded w-1/2 mt-2"></div>
        </div>
      </div>
      <div className="flex gap-2">
        <div className="h-8 bg-slate-700 rounded flex-1"></div>
        <div className="h-8 bg-slate-700 rounded w-10"></div>
      </div>
    </div>
  );
}

export function FileGridSkeleton({ count = 3 }: { count?: number }) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      {Array.from({ length: count }).map((_, i) => (
        <FileSkeleton key={i} />
      ))}
    </div>
  );
}

export function InputSkeleton() {
  return <div className="h-10 bg-slate-700 rounded w-full animate-pulse"></div>;
}
