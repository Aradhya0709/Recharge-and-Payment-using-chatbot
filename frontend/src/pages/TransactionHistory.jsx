import { useState, useEffect } from 'react';
import API from '../api/axios';

export default function TransactionHistory() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    try {
      const res = await API.get('/transactions/recent');
      const data = res.data.data;
      setTransactions(Array.isArray(data) ? data : []);
    } catch (err) {
      console.log('Transaction fetch error:', err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatAmount = (amt) => Number(amt).toLocaleString('en-IN', { minimumFractionDigits: 2 });

  return (
    <div className="bg-slate-800 border border-slate-700 p-6 rounded-2xl max-w-2xl mx-auto">
      <h1 className="text-lg font-bold text-white mb-4">🕒 All Transaction History</h1>
      
      {loading ? (
        <div className="text-sm text-slate-400 text-center py-4">Loading transactions...</div>
      ) : transactions.length === 0 ? (
        <div className="text-sm text-slate-400 text-center py-4">No transactions yet. Start by adding money to your wallet!</div>
      ) : (
        <div className="text-sm text-slate-400 space-y-2 font-mono">
          {transactions.map((txn, idx) => (
            <p key={txn.id || idx}>
              • {txn.transactionRef} - {txn.type?.replace(/_/g, ' ')} - {txn.status} - {txn.type === 'WALLET_TOPUP' ? '+' : '-'}₹{formatAmount(txn.amount)}
            </p>
          ))}
        </div>
      )}
    </div>
  );
}