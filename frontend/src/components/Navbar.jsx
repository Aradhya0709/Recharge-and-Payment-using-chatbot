import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import API from '../api/axios';

export default function Navbar() {
  const { user, logout } = useAuth();
  const [balance, setBalance] = useState(0); 
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    fetchBalance();
    // Refresh balance every 10 seconds
    const interval = setInterval(fetchBalance, 10000);
    return () => clearInterval(interval);
  }, []);

  const fetchBalance = async () => {
    try {
      const res = await API.get('/wallet/balance');
      setBalance(res.data.data.balance || 0);
    } catch (err) {
      // Silently fail — wallet may not exist yet
    }
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-slate-800 border-b border-slate-700 h-16">
      <div className="flex items-center justify-between px-4 md:px-6 h-16">
        
        {/* Logo Layer */}
        <div className="flex items-center gap-3">
          <Link to="/dashboard" className="flex items-center gap-2">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-blue-500 to-indigo-500 flex items-center justify-center">
              <span className="text-white font-bold text-sm">PB</span>
            </div>
            <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-indigo-400 bg-clip-text text-transparent hidden sm:block">PayBot</span>
          </Link>
        </div>

        {/* Right Info Details */}
        <div className="flex items-center gap-4">
          <Link
            to="/wallet"
            className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-700 border border-slate-600 hover:border-blue-500/40 transition-all duration-300 group"
          >
            <span className="text-blue-400 group-hover:scale-110 transition-transform">💼</span>
            <span className="text-sm font-medium text-slate-300">
              ₹{Number(balance).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
            </span>
          </Link>

          <div className="relative">
            <button
              onClick={() => setMenuOpen(!menuOpen)}
              className="flex items-center gap-2 px-3 py-2 rounded-xl hover:bg-slate-700/50 transition-all duration-300"
            >
              <div className="w-8 h-8 rounded-full bg-gradient-to-br from-blue-400 to-indigo-400 flex items-center justify-center">
                <span className="text-white text-sm font-bold">
                  {user?.fullName?.charAt(0)?.toUpperCase() || 'U'}
                </span>
              </div>
              <span className="text-sm font-medium text-slate-300 hidden sm:block">{user?.fullName || 'User'}</span>
            </button>

            {menuOpen && (
              <div className="absolute right-0 top-12 w-48 bg-slate-800 border border-slate-700 rounded-xl p-2 shadow-xl z-50">
                <div className="px-3 py-2 border-b border-slate-700 mb-1">
                  <p className="text-sm font-medium text-white">{user?.fullName || 'PayBot User'}</p>
                  <p className="text-xs text-slate-400">{user?.email || 'user@paybot.com'}</p>
                </div>
                <button
                  onClick={() => {
                    setMenuOpen(false);
                    logout();
                  }}
                  className="w-full flex items-center gap-2 px-3 py-2 text-sm text-red-400 hover:bg-red-500/10 rounded-lg transition-colors"
                >
                  🚪 Sign Out
                </button>
              </div>
            )}
          </div>
        </div>

      </div>
    </nav>
  );
}