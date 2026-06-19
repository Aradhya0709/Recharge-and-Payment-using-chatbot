import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import API from '../api/axios';

export default function Dashboard() {
  const navigate = useNavigate(); // ✅ Page redirection handle karne ke liye hook
  const { user } = useAuth();

  const [balance, setBalance] = useState(0);
  const [recentTxns, setRecentTxns] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [balRes, txnRes] = await Promise.all([
        API.get('/wallet/balance'),
        API.get('/transactions/recent'),
      ]);
      setBalance(balRes.data.data.balance || 0);
      const txnData = txnRes.data.data;
      setRecentTxns(Array.isArray(txnData) ? txnData.slice(0, 5) : []);
    } catch (err) {
      console.log('Dashboard data fetch error:', err.message);
    } finally {
      setLoading(false);
    }
  };

  // ✅ Har service ke liye sahi route path map kar diya hai
  const services = [
    { title: 'Mobile Recharge', desc: 'Prepaid & Postpaid mobile recharge', icon: '📱', grad: 'from-blue-500 to-cyan-500', path: '/recharge/mobile' },
    { title: 'DTH Recharge', desc: 'Recharge satellite TV networks instantly', icon: '📺', grad: 'from-purple-500 to-indigo-500', path: '/recharge/dth' },
    { title: 'Electricity Bill', desc: 'Secure electricity board bill payments', icon: '⚡', grad: 'from-yellow-500 to-orange-500', path: '/bills' },
    { title: 'Water Bill', desc: 'Pay municipality water supply bills easily', icon: '💧', grad: 'from-sky-500 to-blue-600', path: '/bills' },
    { title: 'Gas Bill', desc: 'Pay pipeline or cylinder LPG bills online', icon: '🔥', grad: 'from-emerald-500 to-teal-600', path: '/bills' },
    { title: 'Broadband Bill', desc: 'High-speed internet bill clear clearance', icon: '🌐', grad: 'from-pink-500 to-rose-600', path: '/bills' },
  ];

  const formatAmount = (amt) => Number(amt).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short' });
  };

  return (
    <div className="space-y-8 animate-fade-in p-2">
      
      {/* 1. Header & Quick Balance Section */}
      <div className="flex flex-col lg:flex-row gap-6">
        <div className="flex-1 bg-slate-800/80 border border-slate-700/60 p-6 rounded-2xl backdrop-blur-sm">
          <h1 className="text-2xl font-bold text-white mb-1">
            Welcome back, <span className="bg-gradient-to-r from-blue-400 to-indigo-400 bg-clip-text text-transparent">{user?.fullName || 'User'}</span> 👋
          </h1>
          <p className="text-slate-400 text-sm">Here's your smart fintech payment dashboard summary.</p>
        </div>

        <div className="bg-gradient-to-br from-slate-800 to-slate-900 border border-slate-700 p-6 min-w-[300px] rounded-2xl relative overflow-hidden shadow-xl shadow-blue-950/20">
          <div className="absolute -right-6 -top-6 w-24 h-24 bg-blue-500/10 rounded-full blur-xl"></div>
          <div className="flex items-center gap-3 mb-3">
            <span className="text-xl bg-blue-500/20 p-2 rounded-xl text-blue-400">💳</span>
            <span className="text-sm text-slate-400 font-semibold tracking-wide uppercase">Wallet Balance</span>
          </div>
          <p className="text-3xl font-extrabold text-white tracking-tight">
            ₹{loading ? '---' : formatAmount(balance)}
          </p>
        </div>
      </div>

      {/* 2. Utility Services Grid */}
      <div>
        <h2 className="text-lg font-bold text-slate-200 mb-4 flex items-center gap-2">
          <span className="text-blue-400">🔥</span> Quick Payment Services
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {services.map((s, index) => (
            <div 
              key={index} 
              onClick={() => navigate(s.path)} // ✅ Ab kisi bhi card par click karne par sahi page khuleha
              className="bg-slate-800/90 border border-slate-700/80 p-5 rounded-2xl hover:border-slate-600 hover:-translate-y-1 transition-all duration-300 cursor-pointer group"
            >
              <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${s.grad} flex items-center justify-center text-xl mb-4 shadow-md shadow-black/20 group-hover:scale-110 transition-transform`}>
                {s.icon}
              </div>
              <h3 className="text-base font-bold text-white mb-1 group-hover:text-blue-400 transition-colors">{s.title}</h3>
              <p className="text-xs text-slate-400 leading-relaxed">{s.desc}</p>
            </div>
          ))}
        </div>
      </div>

      {/* 3. Recent Transactions Log */}
      <div>
        <h2 className="text-lg font-bold text-slate-200 mb-4 flex items-center gap-2">
          <span className="text-blue-400">🕒</span> Recent Activities
        </h2>
        <div className="bg-slate-800 border border-slate-700 rounded-2xl overflow-hidden">
          {loading ? (
            <div className="p-6 text-center text-slate-400 text-sm">Loading transactions...</div>
          ) : recentTxns.length === 0 ? (
            <div className="p-6 text-center text-slate-400 text-sm">No transactions yet. Start by adding money to your wallet!</div>
          ) : (
            <div className="divide-y divide-slate-700/50">
              {recentTxns.map((txn, idx) => (
                <div key={txn.id || idx} className="flex items-center justify-between p-4 hover:bg-slate-700/20 transition-all">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-slate-700/60 flex items-center justify-center text-lg">
                      {txn.type === 'WALLET_TOPUP' ? '📥' : '💸'}
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-white">{txn.type?.replace(/_/g, ' ')}</p>
                      <p className="text-xs text-slate-400 font-mono">{txn.transactionRef} • {formatDate(txn.createdAt)}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={`text-sm font-bold ${txn.type === 'WALLET_TOPUP' ? 'text-emerald-400' : 'text-red-400'}`}>
                      {txn.type === 'WALLET_TOPUP' ? '+' : '-'}₹{formatAmount(txn.amount)}
                    </p>
                    <span className="text-[10px] bg-emerald-500/10 text-emerald-400 font-medium px-2 py-0.5 rounded-full border border-emerald-500/20">
                      {txn.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

    </div>
  );
}