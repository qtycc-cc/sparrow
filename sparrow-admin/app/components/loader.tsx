import { Spinner } from "./ui/spinner";

export function Loader() {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 dark:bg-gray-950/70" >
      <Spinner className="size-12" />
    </div>
  );
}