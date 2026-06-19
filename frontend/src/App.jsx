import { Routes, Route, Navigate, Link, useLocation } from "react-router-dom";

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
        {/* 1. SEQUENTIAL AUTHENTICATION FLOW */}
        {/* Default route ab user ko sabse pehle REGISTER/SIGNUP par bhejega */}
        <Route path="/" element={<Navigate to="/signup" replace />} />
        
        <Route path="/signup" element={<Signup />} />
        <Route path="/login" element={<Login />} />

        {/* 2. PROTECTED ROUTE LAYER (ONLY ACCESSIBLE AFTER SUCCESSFUL LOGIN) */}
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <div>
                <Navbar />
                <Sidebar />
                <Chatbot />

                {/* Main Content Render Box with Padding Layout */}
                <div className="pt-20 md:pl-64 p-4">
                  <Routes>
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/wallet" element={<Wallet />} />
                    <Route path="/recharge/mobile" element={<MobileRecharge />} />
                    <Route path="/recharge/dth" element={<DthRecharge />} />
                    <Route path="/bills" element={<BillPayment />} />
                    <Route path="/transactions" element={<TransactionHistory />} />
                    
                    {/* Fallback Internal Router inside Dashboard */}
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