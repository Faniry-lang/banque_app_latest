<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="itu.banque.api.dtos.TauxVariation" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Taux de Variation</title>

    <style>
        body {
            font-family: "Inter", sans-serif;
            background: #f5f5f7; /* blanc casse moderne */
            margin: 0;
            padding: 0;
        }

        .main-content {
            margin-left: 220px; /* coherent avec ton aside */
            padding: 30px;
        }

        h1, h2 {
            color: #333;
            font-weight: 600;
            margin-bottom: 20px;
        }

        /* ✅ Carte / container du formulaire */
        .form-container {
            background: #fff;
            padding: 20px;
            border-radius: 14px;
            border: 1px solid #ddd;
            box-shadow: 0 2px 6px rgba(0,0,0,0.06);
            margin-bottom: 30px;
        }

        .form-container h2 {
            margin: 0 0 15px 0;
            color: #444;
        }

        /* ✅ Champs du formulaire */
        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 500;
            color: #555;
            margin-bottom: 6px;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px 12px;
            font-size: 14px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background: #fafafa;
            transition: border-color 0.2s;
        }

        .form-group input:focus,
        .form-group select:focus {
            border-color: #999;
            outline: none;
        }

        /* ✅ Boutons modernises */
        .btn {
            padding: 8px 14px;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            transition: opacity 0.2s;
        }

        .btn-primary {
            background: #333;
            color: white;
        }

        .btn-primary:hover {
            opacity: 0.85;
        }

        .btn-danger {
            background: #d9534f;
            color: white;
        }

        .btn-danger:hover {
            opacity: 0.85;
        }

        /* ✅ Table modernisee */
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
            border: 1px solid #ddd;
        }

        thead {
            background: #f0f0f0;
        }

        th {
            padding: 12px;
            text-align: left;
            font-size: 14px;
            color: #444;
            border-bottom: 1px solid #ddd;
        }

        td {
            padding: 10px 12px;
            font-size: 14px;
            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #fafafa;
        }

        /* ✅ Inputs dans le tableau */
        td input {
            width: 100%;
            padding: 8px;
            font-size: 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            background: #fafafa;
        }

        td input:focus {
            border-color: #999;
            outline: none;
        }

        /* ✅ Actions dans une même ligne */
        td form {
            display: inline;
        }
    </style>
</head>

<body>
    <%@ include file="aside.jsp" %>

    <div class="main-content">

        <h1>Gestion des Taux de Variation</h1>

        <!-- ✅ Formulaire modernise -->
        <div class="form-container">
            <h2>Ajouter un Taux</h2>

            <form action="taux-variation" method="post">
                <input type="hidden" name="action" value="add">

                <div class="form-group">
                    <label>Devise:</label>
                    <select name="deviseName" required>
                        <% List<String> deviseNames = (List<String>) request.getAttribute("deviseNames");
                           if (deviseNames != null) {
                               for (String name : deviseNames) { %>
                            <option value="<%= name %>"><%= name %></option>
                        <% } } %>
                    </select>
                </div>

                <div class="form-group">
                    <label>Pourcentage:</label>
                    <input type="number" step="0.01" name="pourcentage" required>
                </div>

                <div class="form-group">
                    <label>Date de debut:</label>
                    <input type="date" name="dateDebut" required>
                </div>

                <div class="form-group">
                    <label>Date de fin (optionnel):</label>
                    <input type="date" name="dateFin">
                </div>

                <button type="submit" class="btn btn-primary">Ajouter</button>
            </form>
        </div>

        <!-- ✅ Tableau modernise -->
        <h2>Liste des Taux</h2>

        <table>
            <thead>
                <tr>
                    <th>Devise</th>
                    <th>Pourcentage</th>
                    <th>Date Debut</th>
                    <th>Date Fin</th>
                    <th>Actions</th>
                </tr>
            </thead>

            <tbody>
                <% List<TauxVariation> tauxVariations = (List<TauxVariation>) request.getAttribute("tauxVariations");
                   if (tauxVariations != null) {
                       for (TauxVariation taux : tauxVariations) { %>

                <tr>
                    <form action="taux-variation" method="post">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="ref" value="<%= taux.getRef() %>">

                        <td><input type="text" name="deviseName" value="<%= taux.getDeviseName() %>" required></td>
                        <td><input type="number" step="0.01" name="pourcentage" value="<%= taux.getPourcentage() %>" required></td>
                        <td><input type="date" name="dateDebut" value="<%= taux.getDateDebut() %>" required></td>
                        <td><input type="date" name="dateFin" value="<%= taux.getDateFin() != null ? taux.getDateFin().toString() : "" %>"></td>

                        <td>
                            <button type="submit" class="btn btn-primary">Modifier</button>
                    </form>

                    <form action="taux-variation" method="post">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="ref" value="<%= taux.getRef() %>">
                        <button type="submit" class="btn btn-danger">Supprimer</button>
                    </form>
                        </td>
                </tr>

                <% } } %>
            </tbody>
        </table>

    </div>
</body>
</html>
