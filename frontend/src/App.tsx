    // src/App.tsx
import { AuthProvider } from "./context/AuthContext";
import AppRouter from "./routes/AppRouter";
import { LanguageProvider } from "./context/LanguageContext";

function App() {
  return (
    <LanguageProvider>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </LanguageProvider>
  );
}

export default App;
