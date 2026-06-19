import { useState } from 'react';
import API from '../api/axios';

const popularDthPlans = [
  { amount: 199, channels: '200+', description: 'Basic HD Pack - Popular News & Entertainment' },
  { amount: 349, channels: '300+', description: 'Standard HD Pack - Movies & Sports Included' },
  { amount: 499, channels: '400+', description: 'Premium HD Pack - All Channels + OTT access' },
];

export default function DthRecharge() {
  const [dthId, setDthId] = useState('');
  const [operator, setOperator] = useState('');
  const [amount, setAmount] = useState('');
  const [success, setSuccess] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!dthId || !operator || !amount) return;
    setErrorMsg('');
    setLoading(true);

    try {
      await API.post('/recharge/dth', {
        rechargeType: 'DTH_RECHARGE',
        serviceProvider: operator,
        accountNumber: dthId,
        amount: parseFloat(amount),
      });
      setSuccess(`DTH recharge of ₹${amount} successful! 📺`);
      setTimeout(() => { 
        setSuccess(''); 
        setDthId(''); 
        setOperator('');
        setAmount(''); 
      }, 3000);
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'DTH recharge failed. Check balance.');
      setTimeout(() => setErrorMsg(''), 4000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6 p-2 animate-fade-in">
      <h1 className="text-2xl font-bold text-white">DTH Recharge</h1>
      
      {/* Success Banner Alert */}
      {success && (
        <div className="p-3 bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 rounded-xl text-sm">
          {success}
        </div>
      )}
      {errorMsg && (
        <div className="p-3 bg-red-500/20 text-red-400 border border-red-500/30 rounded-xl text-sm">
          {errorMsg}
        </div>
      )}
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Side: Input Form */}
        <form onSubmit={handleSubmit} className="bg-slate-800 p-6 rounded-2xl border border-slate-700 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">DTH ID / Subscriber ID</label>
            <input 
              type="text" 
              value={dthId} 
              onChange={(e) => setDthId(e.target.value)} 
              placeholder="Enter subscriber ID" 
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
              required 
            />
          </div>
          
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">DTH Operator</label>
            <select 
              value={operator} 
              onChange={(e) => setOperator(e.target.value)} 
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
              required
            >
              <option value="">Select Operator</option>
              <option value="TATA_PLAY">Tata Play</option>
              <option value="AIRTEL_DTH">Airtel Digital TV</option>
              <option value="DISH_TV">Dish TV</option>
              <option value="SUN_DIRECT">Sun Direct</option>
            </select>
          </div>
          
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Amount (₹)</label>
            <input 
              type="number" 
              value={amount} 
              onChange={(e) => setAmount(e.target.value)} 
              placeholder="Enter pack amount" 
              className="w-full bg-slate-700/50 border border-slate-600 rounded-xl p-2.5 text-sm text-white outline-none focus:border-blue-500 transition-all" 
              min="10"
              required 
            />
          </div>
          
          <button 
            type="submit" 
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-500 text-white p-2.5 rounded-xl font-semibold text-sm shadow-lg shadow-blue-600/10 active:scale-[0.99] transition-all disabled:opacity-50"
          >
            {loading ? 'Processing...' : 'Recharge DTH'}
          </button>
        </form>

        {/* Right Side: Popular Plans Selector */}
        <div className="bg-slate-800 p-6 rounded-2xl border border-slate-700 space-y-3">
          <h2 className="text-sm font-bold text-slate-300 mb-2">Popular Subscriptions</h2>
          {popularDthPlans.map((plan, index) => (
            <button 
              key={index} 
              type="button" 
              onClick={() => setAmount(plan.amount.toString())} 
              className="w-full text-left p-3.5 rounded-xl border border-slate-700 bg-slate-700/20 hover:border-blue-500/50 hover:bg-slate-700/40 transition-all group"
            >
              <div className="flex justify-between font-bold text-white text-sm">
                <span className="text-blue-400 font-medium">{plan.channels} Channels</span>
                <span className="text-white">₹{plan.amount}</span>
              </div>
              <p className="text-xs text-slate-400 mt-1.5 leading-relaxed">{plan.description}</p>
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}