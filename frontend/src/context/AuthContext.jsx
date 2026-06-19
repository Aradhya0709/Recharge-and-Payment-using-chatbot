import { createContext, useContext, useState } from "react";
import API from "../api/axios"; // Hamara axios instance jo backend port 8082 par set hai

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(JSON.parse(localStorage.getItem("paybot_user")));
  const [token, setToken] = useState(localStorage.getItem("paybot_token"));
  const [loading, setLoading] = useState(false);

  const signup = async (signupData) => {
    setLoading(true);
    try {
      // Backend ke @PostMapping("/signup") par direct hit maar rahe hain
      const response = await API.post("/auth/signup", signupData);
      setLoading(false);
      return { success: true, data: response.data };
    } catch (error) {
      setLoading(false);
      return { 
        success: false, 
        message: error.response?.data?.message || "Signup failed! Backend check karein." 
      };
    }
  };

  const login = async (email, password) => {
    setLoading(true);
    try {
      // Backend ke @PostMapping("/login") par direct hit maar rahe hain
      const response = await API.post("/auth/login", { email, password });
      
      // Response se JWT token aur user details extract kar rahe hain
      // Backend AuthResponse: { token, email, fullName, message }
      const authData = response.data.data;

      localStorage.setItem("paybot_token", authData.token);
      localStorage.setItem("paybot_user", JSON.stringify({ 
        fullName: authData.fullName, 
        email: authData.email 
      }));

      setToken(authData.token);
      setUser({ fullName: authData.fullName, email: authData.email });
      setLoading(false);
      return { success: true };
    } catch (error) {
      setLoading(false);
      return { 
        success: false, 
        message: error.response?.data?.message || "Invalid credentials or Server down!" 
      };
    }
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, signup, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);