import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import { useState } from 'react'; // Nécessaire pour gérer l'ouverture du menu
import { Dashboard } from './pages/Dashboard';
import { CreateRequest } from './pages/CreateRequest';
import { LandingPage } from './pages/LandingPage';
import { UEList } from './pages/UEList';
import { Profile } from './pages/Profile';
import { UEDetail } from './pages/UEDetail';
import { UserCircle, Menu, X } from 'lucide-react';
import {RequestForm} from "./pages/RequestForm.tsx"; // Ajout des icônes Menu et X

const queryClient = new QueryClient({
    defaultOptions: {
        queries: { retry: 1, refetchOnWindowFocus: false },
    },
});

const Navbar = () => {
    // État pour gérer l'ouverture/fermeture du menu mobile
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    return (
        <nav className="bg-blue-900 text-white shadow-lg sticky top-0 z-50">
            <div className="w-full px-6 lg:px-8">
                <div className="flex justify-between h-20 items-center">

                    {/* --- LOGO --- */}
                    <div className="flex items-center space-x-8">
                        <Link to="/" className="flex items-center space-x-3 group" onClick={() => setIsMenuOpen(false)}>
                            <img
                                src="/images/logo_isfce.png"
                                alt="Logo ISFCE"
                                className="h-12 w-auto rounded p-1 transition-transform group-hover:scale-105"
                                onError={(e) => e.currentTarget.style.display = 'none'}
                            />
                            <div className="flex flex-col">
                                <span className="font-bold text-xl tracking-tight leading-none">ISFCE</span>
                                <span className="text-xs text-blue-200 font-light">Portail des Dispenses</span>
                            </div>
                        </Link>

                        {/* --- MENU BUREAU (Caché sur mobile) --- */}
                        <div className="hidden md:flex space-x-6 ml-8">
                            <Link to="/dashboard" className="text-sm font-medium hover:text-blue-200 transition border-b-2 border-transparent hover:border-blue-400 py-1">
                                Mes Demandes
                            </Link>
                            <Link to="/ues" className="text-sm font-medium hover:text-blue-200 transition border-b-2 border-transparent hover:border-blue-400 py-1">
                                Catalogue UE
                            </Link>
                        </div>
                    </div>

                    {/* --- BOUTONS DROITE --- */}
                    <div className="flex items-center space-x-4">
                        {/* Lien Profil (Bureau) */}
                        <Link to="/profile" className="hidden md:flex items-center space-x-2 bg-blue-800 hover:bg-blue-700 px-4 py-2 rounded-full transition shadow-sm border border-blue-700">
                            <span className="text-sm font-medium">Mon Profil</span>
                            <UserCircle className="w-5 h-5" />
                        </Link>

                        {/* BOUTON BURGER (Mobile uniquement) */}
                        <button
                            className="md:hidden p-2 rounded-md hover:bg-blue-800 transition"
                            onClick={() => setIsMenuOpen(!isMenuOpen)}
                        >
                            {isMenuOpen ? <X className="w-7 h-7" /> : <Menu className="w-7 h-7" />}
                        </button>
                    </div>
                </div>
            </div>

            {/* --- MENU MOBILE DÉROULANT --- */}
            {isMenuOpen && (
                <div className="md:hidden bg-blue-800 border-t border-blue-700 px-4 pt-4 pb-6 space-y-3 shadow-xl">
                    <Link
                        to="/dashboard"
                        className="block text-white font-medium py-2 px-3 rounded hover:bg-blue-700 transition"
                        onClick={() => setIsMenuOpen(false)}
                    >
                        Mes Demandes
                    </Link>
                    <Link
                        to="/ues"
                        className="block text-white font-medium py-2 px-3 rounded hover:bg-blue-700 transition"
                        onClick={() => setIsMenuOpen(false)}
                    >
                        Catalogue UE
                    </Link>
                    <div className="border-t border-blue-700 my-2 pt-2">
                        <Link
                            to="/profile"
                            className="flex items-center space-x-2 text-blue-100 py-2 px-3 rounded hover:bg-blue-700 transition"
                            onClick={() => setIsMenuOpen(false)}
                        >
                            <UserCircle className="w-5 h-5" />
                            <span>Mon Profil</span>
                        </Link>
                    </div>
                </div>
            )}
        </nav>
    );
};

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <BrowserRouter>
                <div className="min-h-screen bg-gray-50 font-sans text-gray-900 flex flex-col">
                    <Navbar />
                    <div className="flex-grow w-full">
                        <Routes>
                            <Route path="/" element={<LandingPage />} />
                            <Route path="*" element={
                                <div className="max-w-[95%] mx-auto px-4 py-8">
                                    <Routes>
                                        <Route path="/dashboard" element={<Dashboard />} />
                                        <Route path="/new" element={<CreateRequest />} />
                                        <Route path="/ues" element={<UEList />} />
                                        <Route path="/profile" element={<Profile />} />
                                        <Route path="/ue/:code" element={<UEDetail />} />
                                        <Route path="/request/:id" element={<RequestForm />} />
                                    </Routes>
                                </div>
                            } />
                        </Routes>
                    </div>
                </div>
            </BrowserRouter>
        </QueryClientProvider>
    );
}

export default App;