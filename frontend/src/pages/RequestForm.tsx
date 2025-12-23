import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../services/api';
// Ajout de l'icône X pour annuler la saisie manuelle
import { Plus, FileText, GraduationCap, Send, ExternalLink, X } from 'lucide-react';
import type { AddExternalCourseDTO, AddDocumentDTO, TypeDocument } from '../types';

export const RequestForm = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    // État pour savoir si on saisit une école manuellement
    const [isCustomSchool, setIsCustomSchool] = useState(false);

    const [courseForm, setCourseForm] = useState<AddExternalCourseDTO>({
        etablissement: '', code: '', libelle: '', ects: 0, urlProgramme: ''
    });

    const [docForm, setDocForm] = useState<AddDocumentDTO>({
        type: 'BULLETIN',
        url: ''
    });

    // 1. Récupération du dossier
    const { data: request, isLoading } = useQuery({
        queryKey: ['request', id],
        queryFn: () => api.getRequestById(id!),
        enabled: !!id
    });

    // 2. Récupération des écoles connues (Pour la liste déroulante)
    const { data: schools } = useQuery({
        queryKey: ['schools'],
        queryFn: api.getSchools
    });

    const addCourseMutation = useMutation({
        mutationFn: (data: AddExternalCourseDTO) => api.addCourse(id!, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['request', id] });
            // Reset du formulaire et du mode "custom school"
            setCourseForm({ etablissement: '', code: '', libelle: '', ects: 0, urlProgramme: '' });
            setIsCustomSchool(false);
        }
    });

    // ... (Mutation Docs et Submit inchangées) ...
    const addDocMutation = useMutation({
        mutationFn: (data: AddDocumentDTO) => api.addDocument(id!, data),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['request', id] });
            setDocForm({ ...docForm, url: '' });
        }
    });

    const submitRequestMutation = useMutation({
        mutationFn: () => api.submitRequest(id!),
        onSuccess: () => navigate('/dashboard')
    });

    // Gestion du changement dans la liste déroulante
    const handleSchoolSelectChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const value = e.target.value;
        if (value === 'OTHER') {
            setIsCustomSchool(true);
            setCourseForm({ ...courseForm, etablissement: '' }); // On vide pour laisser saisir
        } else {
            setIsCustomSchool(false);
            setCourseForm({ ...courseForm, etablissement: value });
        }
    };

    if (isLoading) return <div className="text-center py-12">Chargement...</div>;
    if (!request) return <div className="text-center py-12 text-red-600">Dossier introuvable.</div>;

    const isDraft = request.statut === 'DRAFT';

    return (
        <div className="max-w-5xl mx-auto space-y-8 pb-12">
            {/* ... (Header inchangé) ... */}
            <div className="flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Dossier de dispense</h1>
                    <p className="text-gray-500">Section : <span className="font-medium text-indigo-600">{request.section}</span></p>
                </div>
                <div className="text-sm px-3 py-1 bg-gray-100 rounded-full font-mono text-gray-600">
                    Statut : {request.statut}
                </div>
            </div>

            {/* 1. COURS EXTERNES */}
            <section className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100 bg-gray-50 flex justify-between items-center">
                    <h2 className="font-semibold text-gray-900 flex items-center">
                        <GraduationCap className="w-5 h-5 mr-2 text-indigo-600" />
                        Cours suivis ailleurs (Crédits acquis)
                    </h2>
                </div>

                <div className="p-6 space-y-6">
                    {/* Liste des cours existants (inchangé) */}
                    {request.externalCourses.length > 0 ? (
                        <ul className="space-y-3">
                            {request.externalCourses.map((c) => (
                                <li key={c.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border border-gray-100">
                                    <div>
                                        <p className="font-medium text-gray-900">{c.libelle} <span className="text-gray-400 font-normal">({c.code})</span></p>
                                        <p className="text-sm text-gray-500">{c.etablissement} • {c.ects} ECTS</p>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-400 text-sm italic">Aucun cours ajouté pour le moment.</p>
                    )}

                    {/* --- FORMULAIRE D'AJOUT MODIFIÉ --- */}
                    {isDraft && (
                        <div className="bg-indigo-50 p-4 rounded-lg border border-indigo-100">
                            <h3 className="text-sm font-medium text-indigo-900 mb-3">Ajouter un cours</h3>
                            <div className="grid grid-cols-1 md:grid-cols-6 gap-3">

                                {/* CHAMP ÉCOLE : LISTE ou INPUT */}
                                <div className="md:col-span-2 relative">
                                    {!isCustomSchool ? (
                                        <div className="relative">
                                            <select
                                                className="w-full p-2 rounded border border-gray-300 text-sm bg-white appearance-none cursor-pointer"
                                                value={courseForm.etablissement}
                                                onChange={handleSchoolSelectChange}
                                            >
                                                <option value="" disabled>Choisir une école...</option>
                                                {schools?.map((school) => (
                                                    <option key={school.code} value={school.code}>
                                                        {school.etablissement}
                                                    </option>
                                                ))}
                                                <option value="OTHER" className="font-bold text-indigo-600">
                                                    + Autre (Saisie manuelle)
                                                </option>
                                            </select>
                                            {/* Petite flèche */}
                                            <div className="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none text-gray-500">
                                                <svg className="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" /></svg>
                                            </div>
                                        </div>
                                    ) : (
                                        <div className="flex gap-2">
                                            <input
                                                type="text"
                                                placeholder="Nom de l'école..."
                                                className="w-full p-2 rounded border border-gray-300 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
                                                value={courseForm.etablissement}
                                                onChange={e => setCourseForm({...courseForm, etablissement: e.target.value})}
                                                autoFocus
                                            />
                                            <button
                                                onClick={() => setIsCustomSchool(false)}
                                                className="p-2 text-gray-500 hover:text-red-500 bg-white border border-gray-300 rounded hover:bg-red-50 transition"
                                                title="Revenir à la liste"
                                            >
                                                <X className="w-4 h-4" />
                                            </button>
                                        </div>
                                    )}
                                </div>

                                <input type="text" placeholder="Code (ex: INFO-F-101)" className="md:col-span-1 p-2 rounded border border-gray-300 text-sm"
                                       value={courseForm.code} onChange={e => setCourseForm({...courseForm, code: e.target.value})} />
                                <input type="text" placeholder="Nom du cours" className="md:col-span-2 p-2 rounded border border-gray-300 text-sm"
                                       value={courseForm.libelle} onChange={e => setCourseForm({...courseForm, libelle: e.target.value})} />
                                <input type="number" placeholder="ECTS" className="md:col-span-1 p-2 rounded border border-gray-300 text-sm"
                                       value={courseForm.ects || ''} onChange={e => setCourseForm({...courseForm, ects: parseInt(e.target.value)})} />

                                <button
                                    onClick={() => addCourseMutation.mutate(courseForm)}
                                    disabled={!courseForm.code || !courseForm.libelle || !courseForm.etablissement}
                                    className="md:col-span-6 bg-indigo-600 text-white rounded p-2 text-sm font-medium hover:bg-indigo-700 disabled:bg-indigo-300 transition flex items-center justify-center mt-2"
                                >
                                    <Plus className="w-4 h-4 mr-2" /> Ajouter ce cours
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </section>

            {/* ... (Section Documents et Bouton Soumettre inchangés) ... */}
            <section className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-100 bg-gray-50 flex justify-between items-center">
                    <h2 className="font-semibold text-gray-900 flex items-center">
                        <FileText className="w-5 h-5 mr-2 text-indigo-600" />
                        Documents Justificatifs
                    </h2>
                </div>

                <div className="p-6 space-y-6">
                    {/* Liste des documents */}
                    {request.documents.length > 0 ? (
                        <ul className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                            {request.documents.map((d) => (
                                <li key={d.id} className="flex items-center p-3 bg-gray-50 rounded-lg border border-gray-100 text-sm">
                                    <div className="bg-white p-2 rounded border border-gray-200 mr-3">
                                        <FileText className="w-4 h-4 text-gray-400" />
                                    </div>
                                    <div className="flex-grow truncate">
                                        <p className="font-medium text-gray-900">{d.type}</p>
                                        <a href={d.urlStockage} target="_blank" rel="noreferrer" className="text-indigo-600 hover:underline flex items-center text-xs">
                                            Voir le fichier <ExternalLink className="w-3 h-3 ml-1" />
                                        </a>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-gray-400 text-sm italic">Aucun document joint.</p>
                    )}

                    {/* Formulaire Ajout Doc */}
                    {isDraft && (
                        <div className="flex gap-3 items-end bg-gray-50 p-4 rounded-lg border border-gray-200">
                            <div className="flex-grow">
                                <label className="block text-xs font-medium text-gray-500 mb-1">Type de document</label>
                                <select
                                    className="w-full p-2 rounded border border-gray-300 text-sm"
                                    value={docForm.type}
                                    onChange={e => setDocForm({...docForm, type: e.target.value as TypeDocument})}
                                >
                                    <option value="BULLETIN">Bulletin de notes (Relevé)</option>
                                    <option value="PROGRAMME">Programme des cours (Descriptif)</option>
                                    <option value="MOTIVATION">Lettre de motivation</option>
                                    <option value="AUTRE">Autre</option>
                                </select>
                            </div>
                            <div className="flex-grow-[2]">
                                <label className="block text-xs font-medium text-gray-500 mb-1">Lien vers le fichier (URL)</label>
                                <input
                                    type="text"
                                    placeholder="https://..."
                                    className="w-full p-2 rounded border border-gray-300 text-sm"
                                    value={docForm.url}
                                    onChange={e => setDocForm({...docForm, url: e.target.value})}
                                />
                            </div>
                            <button
                                onClick={() => addDocMutation.mutate(docForm)}
                                disabled={!docForm.url}
                                className="bg-gray-800 text-white px-4 py-2 rounded text-sm font-medium hover:bg-gray-900 disabled:opacity-50 transition"
                            >
                                Ajouter
                            </button>
                        </div>
                    )}
                </div>
            </section>

            {isDraft && (
                <div className="flex justify-end pt-4">
                    <button
                        onClick={() => submitRequestMutation.mutate()}
                        disabled={request.externalCourses.length === 0 || request.documents.length === 0}
                        className="flex items-center px-6 py-3 bg-green-600 text-white rounded-xl font-bold shadow-lg hover:bg-green-700 hover:shadow-xl transform hover:-translate-y-0.5 transition disabled:bg-gray-300 disabled:shadow-none disabled:translate-y-0 disabled:cursor-not-allowed"
                    >
                        <Send className="w-5 h-5 mr-2" />
                        Soumettre ma demande
                    </button>
                </div>
            )}
        </div>
    );
};