<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="itu.banque.api.dtos.Devise" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Devises</title>

    <!-- Lucide Icons CDN -->
    <script src="https://unpkg.com/lucide@latest"></script>

    <style>
        body {
            margin: 0;
            font-family: sans-serif;
            background-color: #f4f4f4;
        }

        .main-content {
            margin-left: 200px;
            padding: 20px;
        }

        h1, h2 {
            color: #333;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
        }

        /* FORMULAIRE */
        .form-container {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 1px 4px rgba(0,0,0,0.1);
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 12px;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            color: #555;
            margin-bottom: 6px;
        }

        .form-group input {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 15px;
            background-color: #fafafa;
        }

        /* BOUTONS */
        .btn {
            padding: 10px 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            color: white;
            font-size: 14px;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn svg { width: 18px; height: 18px; stroke-width: 2.2; }

        .btn-primary { background-color: #222; }
        .btn-primary:hover { background-color: #000; }

        .btn-danger { background-color: #dc3545; }
        .btn-danger:hover { background-color: #c82333; }

        /* TABLE */
        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 1px 4px rgba(0,0,0,0.1);
        }

        th {
            background: #fafafa;
            text-align: left;
            padding: 12px;
            font-weight: 600;
            color: #444;
        }

        td {
            padding: 12px;
            border-top: 1px solid #eee;
        }

        tr:nth-child(even) {
            background-color: #fcfcfc;
        }

        td input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 6px;
        }

        /* MODAL */
        .modal {
            display: none;
            position: fixed;
            z-index: 20;
            left: 0; top: 0;
            width: 100%; height: 100%;
            background-color: rgba(0,0,0,0.4);
            justify-content: center;
            align-items: center;
        }

        .modal-content {
            background: white;
            padding: 20px;
            width: 90%;
            max-width: 450px;
            border-radius: 10px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .close-button {
            position: absolute;
            right: 15px;
            top: 10px;
            font-size: 26px;
            cursor: pointer;
            color: #777;
        }

        .close-button:hover { color: #000; }
    </style>
</head>
<body>

<%@ include file="aside.jsp" %>

<div class="main-content">

    <h1>Gestion des Devises</h1>

    <!-- AJOUTER -->
    <div class="form-container">
        <h2>Ajouter une Devise</h2>
        <form action="devises" method="post">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="nom">Nom :</label>
                <input type="text" id="nom" name="nom" required>
            </div>

            <div class="form-group">
                <label for="montant">Montant :</label>
                <input type="number" step="0.01" id="montant" name="montant" required>
            </div>

            <div class="form-group">
                <label for="dateDebut">Date de debut :</label>
                <input type="date" id="dateDebut" name="dateDebut" required>
            </div>

            <div class="form-group">
                <label for="dateFin">Date de fin (optionnel) :</label>
                <input type="date" id="dateFin" name="dateFin">
            </div>

            <button type="submit" class="btn btn-primary">
                <i data-lucide="plus-circle"></i> Ajouter
            </button>
        </form>
    </div>

    <h2>Liste des Devises</h2>

    <table>
        <thead>
            <tr>
                <th>Nom</th>
                <th>Montant</th>
                <th>Date Debut</th>
                <th>Date Fin</th>
                <th>Actions</th>
            </tr>
        </thead>

        <tbody>
        <% 
            List<Devise> devises = (List<Devise>) request.getAttribute("devises");
            if (devises != null) {
                for (Devise devise : devises) {
        %>
            <tr>
                <form action="devises" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="ref" value="<%= devise.getRef() %>">

                    <td><input type="text" name="nom" value="<%= devise.getNom() %>" required></td>
                    <td><input type="number" step="0.01" name="montant" value="<%= devise.getMontant() %>" required></td>
                    <td><input type="date" name="dateDebut" value="<%= devise.getDateDebut() %>" required></td>
                    <td><input type="date" name="dateFin" value="<%= devise.getDateFin() != null ? devise.getDateFin().toString() : "" %>"></td>

                    <td>
                        <button type="submit" class="btn btn-primary">
                            <i data-lucide="pencil"></i> Modifier
                        </button>
                </form>

                <form action="devises" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="ref" value="<%= devise.getRef() %>">
                    <button type="submit" class="btn btn-danger">
                        <i data-lucide="trash-2"></i> Supprimer
                    </button>
                </form>
                    </td>
            </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>

<!-- ERREUR MODAL -->
<div id="errorModal" class="modal">
    <div class="modal-content">
        <span class="close-button">&times;</span>
        <h2>Erreur</h2>
        <p id="errorMessage"></p>
    </div>
</div>

<script>
    lucide.createIcons();

    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        const error = urlParams.get('error');
        if (error) {
            document.getElementById('errorMessage').innerText = decodeURIComponent(error);
            document.getElementById('errorModal').style.display = 'flex';
        }

        const modal = document.getElementById('errorModal');
        const closeButton = document.querySelector('.close-button');

        closeButton.onclick = closeModal;
        window.onclick = (e) => { if (e.target == modal) closeModal() };

        function closeModal() {
            modal.style.display = 'none';
            const newUrl = window.location.pathname;
            window.history.replaceState({}, '', newUrl);
        }
    }
</script>

</body>
</html>
