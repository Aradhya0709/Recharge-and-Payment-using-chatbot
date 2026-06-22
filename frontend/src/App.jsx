import { Routes, Route, Navigate, useLocation } from "react-router-dom";

import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import Wallet from "./pages/Wallet";
import MobileRecharge from "./pages/MobileRecharge";
import DthRecharge from "./pages/DThRecharge"; 
import BillPayment from "./pages/BillPayment";
import TransactionHistory from "./pages/TransactionHistory";

import ProtectedRoute from "./components/ProtectedRoute";

import Navbar from "./components/Navbar";
import Sidebar from "./components/Sidebar";
import Chatbot from "./components/Chatbot"; 

export default function App() {
  const location = useLocation();

  return (
    <div className="min-h-screen bg-slate-900 text-white">
      <Routes>
        {/* Default route ab Welcome page dikhayega */}
        <Route path="/" element={<Landing />} /> 
        
        <Route path="/signup" element={<Signup />} />
        <Route path="/login" element={<Login />} />

        {/* Protected Routing (No touch area) */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <div>
                <Navbar />
                <Sidebar />
                <Chatbot />

                <div className="pt-20 md:pl-64 p-4">
                  <Routes>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/wallet" element={<Wallet />} />
                    <Route path="/recharge/mobile" element={<MobileRecharge />} />
                    <Route path="/recharge/dth" element={<DthRecharge />} />
                    <Route path="/bills" element={<BillPayment />} />
                    <Route path="/transactions" element={<TransactionHistory />} />
                    <Route path="*" element={<Navigate to="/dashboard" replace />} />
                  </Routes>
                </div>
              </div>
            </ProtectedRoute>
          }
        />
      </Routes>
    </div>
  );
}