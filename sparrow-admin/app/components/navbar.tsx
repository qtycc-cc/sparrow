import { Bird } from "lucide-react";
import { ModeToggle } from "./mode-toggle";
import { Link } from "react-router";

/**
 * Navbar component
 * @returns {JSX.Element}
 */
export default function Navbar() {
  return (
    <div>
      {/* Navbar */}
      <nav className="fixed top-0 left-0 right-0 h-16 bg-white/70 dark:bg-gray-900/70 backdrop-blur-md border-b border-gray-200 dark:border-gray-700 flex items-center justify-between px-4 shadow-sm z-10">
        <Link to="/">
          <div className="text-lg font-semibold flex items-center gap-2">
            <Bird />
            <span>Sparrow 配置中心</span>
          </div>
        </Link>
        <ModeToggle />
      </nav>
      {/* Spacer for Navbar */}
      <div className="h-16" />
    </div>
  );
}