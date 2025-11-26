import { Link } from "react-router";


export default function Home() {
  return (
    <>
      <div>Welcome to the Sparrow Admin Dashboard</div>
      <Link to="/app" className="underline decoration-solid">Go to App Management</Link>
    </>
  );
}