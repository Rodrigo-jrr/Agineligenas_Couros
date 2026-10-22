export function ProductCardSkeleton() {
  return (
    <div className="flex flex-col rounded-2xl border border-beige-300 bg-white/60 p-6">
      <div className="mb-4 h-32 animate-pulse rounded-xl bg-beige-200" />
      <div className="mb-3 h-5 w-2/3 animate-pulse rounded bg-beige-200" />
      <div className="mb-2 h-3 w-full animate-pulse rounded bg-beige-200" />
      <div className="mb-4 h-3 w-4/5 animate-pulse rounded bg-beige-200" />
      <div className="h-5 w-1/3 animate-pulse rounded bg-beige-200" />
    </div>
  )
}
