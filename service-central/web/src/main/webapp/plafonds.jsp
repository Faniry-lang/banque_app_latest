<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="itu.banque.api.dtos.PlafondDto" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Plafonds</title>

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

    <h1>Gestion des Plafonds</h1>

    <!-- AJOUTER -->
    <div class="form-container">
        <h2>Ajouter un plafond</h2>
        <form action="plafonds" method="post">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="idCompte">idCompte :</label>
                <select name="idCompte" id="idCompte">
                    <option value="">Général</option>
                    <c:forEach var="c" items="${compteCourants}">
                        <option value="${c.id}">${c.id}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="idTypeTransaction">Type transaction :</label>
                <select name="idTypeTransaction" id="idTypeTransaction">
                    <c:forEach var="t" items="${typeTransactions}">
                        <option value="${t.id}">${t.nom}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="idFrequencePlafond">Frequence plafond :</label>
                <select name="idFrequencePlafond" id="idFrequencePlafond">
                    <c:forEach var="f" items="${frequencePlafonds}">
                        <option value="${f.id}">${f.libelle}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="idContexteTransaction">Contexte transaction :</label>
                <select name="idContexteTransaction" id="idContexteTransaction">
                    <c:forEach var="c" items="${contexteTransactions}">
                        <option value="${c.id}">${c.libelle}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="dateDebut">Date de debut :</label>
                <input type="date" id="dateDebut" name="dateDebut" required>
            </div>

            <div class="form-group">
                <label for="dateFin">Date de fin (optionnel) :</label>
                <input type="date" id="dateFin" name="dateFin">
            </div>

            <div class="form-group">
                <label>Montant:</label>
                <input type="number" step="0.01" name="montant" required>
            </div>

            <button type="submit" class="btn btn-primary">
                <i data-lucide="plus-circle"></i> Ajouter
            </button>
        </form>
    </div>

    <h2>Liste des Plafonds</h2>

    <table>
        <thead>
            <tr>
                <th>Id</th>
                <th>Montant</th>
                <th>Compte</th>
                <th>Type transaction</th>
                <th>Frequence plafond</th>
                <th>Contexte transaction</th>
                <th>Date Debut</th>
                <th>Date Fin</th>
                <th>Actions</th>
            </tr>
        </thead>

        <tbody>
        <% 
            List<PlafondDto> plafonds = (List<PlafondDto>) request.getAttribute("plafonds");
            if (plafonds != null) {
                for (PlafondDto plafond : plafonds) {
        %>
            <tr>
                <form action="plafonds" method="post">
                    <td><input type="text" name="id" value="<%= plafond.getId() %>" required></td>
                    <td><input type="number" step="0.01" name="montant" value="<%= plafond.getMontant() %>" required></td>
                    <td><input type="text" name="idCompte" value="<%= plafond.getIdCompte() %>" required></td>
                    <td><input type="text" name="idTypeTransaction" value="<%= plafond.getIdTypeTransaction() %>" required></td>
                    <td><input type="text" name="idFrequencePlafond" value="<%= plafond.getIdFrequencePlafond() %>" required></td>
                    <td><input type="text" name="idContexteTransaction" value="<%= plafond.getIdContexteTransaction() %>" required></td>
                    <td><input type="date" name="dateDebut" value="<%= plafond.getDateDebut() %>" required></td>
                    <td><input type="date" name="dateFin" value="<%= plafond.getDateFin() != null ? plafond.getDateFin().toString() : "" %>"></td>

                    <!-- <td>
                        <button type="submit" class="btn btn-primary">
                            <i data-lucide="pencil"></i> Modifier
                        </button>
                    </td> -->
                </form>

                <!-- <form action="plafonds" method="post" style="display:inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="<%= plafond.getId() %>">
                    <button type="submit" class="btn btn-danger">
                        <i data-lucide="trash-2"></i> Supprimer
                    </button>
                </form> -->
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
