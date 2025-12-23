import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query'; // Import nécessaire
import { api } from '../services/api';
import { ArrowLeft, Save } from 'lucide-react';

export const CreateRequest = () => {
    const [section, setSection] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const navigate = useNavigate();

    // 1. Récupérer les sections depuis le backend
    const { data: sections, isLoading } = useQuery({
        queryKey: ['sections'],
        queryFn: api.getSections
    });

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!section) return;

        setIsSubmitting(true);
        try {
            // 1. On crée la demande et ON RÉCUPÈRE le résultat (qui contient l'ID)
            const newRequest = await api.createRequest(section);

            // 2. Redirection vers la page d'édition de ce dossier spécifique
            navigate(`/request/${newRequest.id}`);
        } catch (error) {
            alert("Erreur lors de la création du dossier !");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto space-y-6">
            <button
                onClick={() => navigate(-1)}
                className="text-gray-500 hover:text-gray-700 flex items-center transition"
            >
                <ArrowLeft className="w-4 h-4 mr-1" />
                Retour
            </button>

            <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-200">
                <h1 className="text-2xl font-bold text-gray-900 mb-6">
                    Commencer un nouveau dossier
                </h1>

                <form onSubmit={handleSubmit} className="space-y-6">
                    <div>
                        <label htmlFor="section" className="block text-sm font-medium text-gray-700 mb-1">
                            Section d'études
                        </label>

                        {/* 2. Liste Déroulante (Select) */}
                        <div className="relative">
                            <select
                                id="section"
                                value={section}
                                onChange={(e) => setSection(e.target.value)}
                                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 outline-none transition bg-white appearance-none cursor-pointer"
                                required
                                disabled={isLoading}
                            >
                                <option value="" disabled>
                                    {isLoading ? "Chargement des sections..." : "Sélectionnez votre section"}
                                </option>

                                {sections?.map((sec) => (
                                    <option key={sec.code} value={sec.code}>
                                        {sec.label}
                                    </option>
                                ))}
                            </select>

                            {/* Petite flèche pour faire joli */}
                            <div className="absolute inset-y-0 right-0 flex items-center px-4 pointer-events-none text-gray-500">
                                <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20">
                                    <path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" />
                                </svg>
                            </div>
                        </div>

                        <p className="mt-2 text-sm text-gray-500">
                            Choisissez la section dans laquelle vous êtes inscrit(e).
                        </p>
                    </div>

                    <button
                        type="submit"
                        disabled={isSubmitting || !section}
                        className={`w-full flex justify-center items-center px-4 py-3 rounded-lg text-white font-medium transition
              ${(isSubmitting || !section)
                            ? 'bg-indigo-400 cursor-not-allowed'
                            : 'bg-indigo-600 hover:bg-indigo-700 shadow-sm'
                        }`}
                    >
                        {isSubmitting ? (
                            <span>Création en cours...</span>
                        ) : (
                            <>
                                <Save className="w-4 h-4 mr-2" />
                                Créer le dossier
                            </>
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
};