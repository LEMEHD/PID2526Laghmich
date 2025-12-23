import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import { BookOpen, Search, Filter } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useState, useMemo } from 'react';

export const UEList = () => {
    const [searchTerm, setSearchTerm] = useState('');
    // Par défaut 'INFO', ou '' pour "Toutes"
    const [selectedSection, setSelectedSection] = useState('INFO');

    const navigate = useNavigate();

    const { data: ues, isLoading: loadingUEs } = useQuery({
        queryKey: ['ues'],
        queryFn: api.getAllUEs
    });

    const { data: sections } = useQuery({
        queryKey: ['sections'],
        queryFn: api.getSections
    });

    // --- LOGIQUE DE FILTRAGE CORRIGÉE ---
    const filteredUes = useMemo(() => {
        if (!ues) return [];

        return ues.filter(ue => {
            // 1. Filtre Texte
            const matchesSearch =
                ue.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
                ue.code.toLowerCase().includes(searchTerm.toLowerCase());

            // 2. Filtre Section
            // Si "Toutes" est sélectionné (vide), on garde tout.
            // Sinon, on regarde si la liste des sections de l'UE contient celle sélectionnée.
            const matchesSection =
                selectedSection === '' ||
                (ue.sections && ue.sections.some(sec => sec.code === selectedSection));

            return matchesSearch && matchesSection;
        });
    }, [ues, searchTerm, selectedSection]);

    return (
        <div className="space-y-6">
            {/* En-tête */}
            <div className="flex justify-between items-center flex-wrap gap-4">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900 flex items-center">
                        <BookOpen className="w-8 h-8 mr-3 text-indigo-600" />
                        Catalogue des Cours (UE)
                    </h1>
                    <p className="text-gray-500 mt-1">Liste officielle des Unités d'Enseignement</p>
                </div>
                <Link to="/" className="text-indigo-600 hover:text-indigo-800 font-medium">
                    Retour à l'accueil
                </Link>
            </div>

            {/* Barre d'outils */}
            <div className="flex flex-col md:flex-row gap-4 bg-white p-4 rounded-xl shadow-sm border border-gray-200">

                {/* Recherche Texte */}
                <div className="relative flex-grow">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                    <input
                        type="text"
                        placeholder="Rechercher un cours par nom ou code..."
                        className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>

                {/* Liste Déroulante Sections */}
                <div className="relative min-w-[300px]">
                    <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-gray-500">
                        <Filter className="w-4 h-4" />
                    </div>
                    <select
                        value={selectedSection}
                        onChange={(e) => setSelectedSection(e.target.value)}
                        className="w-full pl-10 pr-8 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none appearance-none bg-white cursor-pointer"
                    >
                        <option value="">Toutes les sections</option>
                        {sections?.map((sec) => (
                            <option key={sec.code} value={sec.code}>
                                {sec.label}
                            </option>
                        ))}
                    </select>
                    {/* Flèche custom */}
                    <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none text-gray-500">
                        <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                            <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                        </svg>
                    </div>
                </div>
            </div>

            {/* Tableau */}
            {loadingUEs ? (
                <div className="flex justify-center py-12">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
                </div>
            ) : (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom de l'UE</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Périodes</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ECTS</th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Niveau</th>
                        </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                        {filteredUes.length > 0 ? (
                            filteredUes.map((ue) => (
                                <tr
                                    key={ue.id}
                                    onClick={() => navigate(`/ue/${ue.code}`)}
                                    className="hover:bg-indigo-50 transition-colors cursor-pointer group"
                                >
                                    <td className="px-6 py-4 whitespace-nowrap font-mono text-sm text-indigo-600 font-medium group-hover:text-indigo-800">
                                        {ue.code}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
                                        {ue.nom}
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        {ue.nbPeriodes} p.
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <span className="px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                          {ue.ects} ECTS
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full 
                                            ${ue.niveau === 1 ? 'bg-green-100 text-green-800' :
                                            ue.niveau === 2 ? 'bg-yellow-100 text-yellow-800' :
                                                'bg-red-100 text-red-800'}`}>
                                            B{ue.niveau}
                                        </span>
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={5} className="px-6 py-12 text-center text-gray-500">
                                    Aucun cours trouvé pour cette recherche.
                                </td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};