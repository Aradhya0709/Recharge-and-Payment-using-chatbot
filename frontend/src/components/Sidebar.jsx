import { Link, useLocation } from 'react-router-dom';

const navItems = [
  { path: '/dashboard', label: '📊 Dashboard' },
  { path: '/recharge/mobile', label: '📱 Mobile Recharge' },
  { path: '/recharge/dth', label: '📺 DTH Recharge' },
  { path: '/bills', label: '🧾 Pay Bills' },
  { path: '/wallet', label: '💳 My Wallet' },
  { path: '/transactions', label: '🕒 Transactions' },
];

export default function Sidebar() {
  const location = useLocation();

  return (
    <aside className="fixed top-16 left-0 bottom-0 w-64 z-40 bg-slate-800 border-r border-slate-700 hidden md:block">
      <nav className="p-4 pt-6 space-y-1">
        {navItems.map((item) => {
          const isActive = location.pathname.startsWith(item.path);
          return (
            <Link
              key={item.path}
              to={item.path}
              className={`flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                isActive 
                  ? 'bg-blue-600/20 text-blue-400 font-semibold border border-blue-500/30' 
                  : 'text-slate-400 hover:text-white hover:bg-slate-700/50'
              }`}
            >
              <span className="text-sm font-medium">{item.label}</span>
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}