// src/pages/UEDetail.tsx
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { api } from '../services/api';
import { ArrowLeft, BookOpen, Clock, Award, BarChart } from 'lucide-react';

export const UEDetail = () => {
    const { code } = useParams<{ code: string }>(); // On récupère le code de l'URL (ex: IPAP)
    const navigate = useNavigate();

    const { data: ue, isLoading, isError } = useQuery({
        queryKey: ['ue', code],
        queryFn: () => api.getOneUE(code!),
        enabled: !!code, // Ne lance la requête que si le code est présent
    });

    if (isLoading) return (
        <div className="flex justify-center p-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
        </div>
    );

    if (isError || !ue) return (
        <div className="text-center py-12">
            <h2 className="text-xl font-bold text-red-600">UE introuvable</h2>
            <button onClick={() => navigate('/ues')} className="mt-4 text-indigo-600 hover:underline">
                Retour au catalogue
            </button>
        </div>
    );

    return (
        <div className="max-w-4xl mx-auto space-y-6">
            {/* Bouton retour */}
            <Link to="/ues" className="inline-flex items-center text-gray-500 hover:text-indigo-600 transition mb-4">
                <ArrowLeft className="w-4 h-4 mr-2" />
                Retour au catalogue
            </Link>

            {/* En-tête de la fiche */}
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="bg-indigo-600 px-8 py-6">
                    <div className="flex justify-between items-start text-white">
                        <div>
                            <span className="inline-block bg-indigo-500/50 border border-indigo-400/50 rounded-lg px-3 py-1 text-sm font-mono mb-2">
                                {ue.code}
                            </span>
                            <h1 className="text-3xl font-bold">{ue.nom}</h1>
                            {ue.ref && <p className="text-indigo-200 mt-1 text-sm">Ref: {ue.ref}</p>}
                        </div>
                        <div className="text-center bg-white/10 rounded-lg p-3 backdrop-blur-sm">
                            <span className="block text-2xl font-bold">{ue.ects}</span>
                            <span className="text-xs uppercase opacity-80">ECTS</span>
                        </div>
                    </div>
                </div>

                {/* Infos Clés */}
                <div className="grid grid-cols-3 divide-x divide-gray-100 border-b border-gray-100">
                    <div className="p-4 flex items-center justify-center space-x-3">
                        <Clock className="text-gray-400 w-5 h-5" />
                        <div>
                            <p className="text-xs text-gray-500 uppercase font-bold">Périodes</p>
                            <p className="text-gray-900 font-medium">{ue.nbPeriodes} p.</p>
                        </div>
                    </div>
                    <div className="p-4 flex items-center justify-center space-x-3">
                        <BarChart className="text-gray-400 w-5 h-5" />
                        <div>
                            <p className="text-xs text-gray-500 uppercase font-bold">Niveau</p>
                            <p className="text-gray-900 font-medium">Bloc {ue.niveau}</p>
                        </div>
                    </div>
                    <div className="p-4 flex items-center justify-center space-x-3">
                        <Award className="text-gray-400 w-5 h-5" />
                        <div>
                            <p className="text-xs text-gray-500 uppercase font-bold">Crédits</p>
                            <p className="text-gray-900 font-medium">{ue.ects}</p>
                        </div>
                    </div>
                </div>

                <div className="p-8 space-y-8">
                    {/* Programme */}
                    <section>
                        <h2 className="text-xl font-bold text-gray-900 mb-4 flex items-center">
                            <BookOpen className="w-5 h-5 mr-2 text-indigo-600" />
                            Programme
                        </h2>
                        <div className="prose text-gray-600 max-w-none whitespace-pre-line">
                            {ue.prgm || <span className="italic text-gray-400">Aucun programme disponible.</span>}
                        </div>
                    </section>

                    {/* Acquis d'apprentissage */}
                    {ue.acquis && ue.acquis.length > 0 && (
                        <section>
                            <h2 className="text-xl font-bold text-gray-900 mb-4">Acquis d'apprentissage</h2>
                            <div className="grid gap-3">
                                {ue.acquis.map((acquis, index) => (
                                    <div key={index} className="flex items-start bg-gray-50 p-4 rounded-lg border border-gray-100">
                                        <div className="flex-shrink-0 bg-green-100 text-green-700 font-bold text-xs px-2 py-1 rounded mt-0.5">
                                            {acquis.pourcentage}%
                                        </div>
                                        <p className="ml-3 text-gray-700 text-sm">{acquis.description}</p>
                                    </div>
                                ))}
                            </div>
                        </section>
                    )}
                </div>
            </div>
        </div>
    );
};