<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Transactions</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f9f9f9; }
        h1 { color: #333; }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:nth-child(even) { background-color: #f2f2f2; }
        tr:hover { background-color: #e9ecef; }
        .modal {
            display: none; /* Hidden by default */
            position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%;
            overflow: auto; background-color: rgba(0,0,0,0.5);
            align-items: center; justify-content: center;
        }
        .modal-content {
            background-color: #fff;
            margin: auto;
            padding: 30px;
            border: 1px solid #888;
            width: 90%;
            max-width: 500px;
            border-radius: 8px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
            position: relative;
        }
        .close {
            color: #aaa;
            position: absolute;
            top: 10px;
            right: 20px;
            font-size: 28px;
            font-weight: bold;
        }
        .close:hover, .close:focus { color: black; text-decoration: none; cursor: pointer; }
        .button {
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-bottom: 20px;
            font-size: 16px;
        }
        .button:hover { background-color: #218838; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input, .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>

    <h1>Liste des Transactions</h1>

    <c:if test="${canCreate}">
        <a href="transactions?openModal=true" class="button">Nouvelle Transaction</a>
    </c:if>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Compte</th>
                <th>Montant</th>
                <th>Type</th>
                <th>Date</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="t" items="${transactions}">
                <tr>
                    <td>${t.id}</td>
                    <td>${t.idCompte}</td>
                    <td>${t.montant}</td>
                    <td>${t.idTypeTransaction}</td>
                    <td>${t.dateTransaction}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty transactions}">
                <tr><td colspan="5">Aucune transaction trouvée.</td></tr>
            </c:if>
        </tbody>
    </table>

    <c:if test="${openModal}">
        <div id="addTransactionModal" class="modal" style="display:flex;">
            <div class="modal-content">
                <span class="close" onclick="document.getElementById('addTransactionModal').style.display='none'">&times;</span>
                <h2>Nouvelle Transaction</h2>
                <form action="transactions" method="post">
                    <div class="form-group">
                        <label for="idCompte">Compte:</label>
                        <select id="idCompte" name="idCompte" required>
                            <c:forEach var="c" items="${comptes}">
                                <option value="${c.id}">${c.numeroCompte} - ${c.nomProprietaire}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="montant">Montant:</label>
                        <input type="number" step="0.01" id="montant" name="montant" required>
                    </div>
                    <div class="form-group">
                        <label for="nomDevise">Devise:</label>
                        <select id="nomDevise" name="nomDevise" required>
                            <c:forEach var="nom" items="${devises}">
                                <option value="${nom}">${nom}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="dateTransaction">Date de la transaction:</label>
                        <input type="date" id="dateTransaction" name="dateTransaction" required>
                    </div>
                    <div class="form-group">
                        <label for="idTypeTransaction">Type de Transaction:</label>
                        <select id="idTypeTransaction" name="idTypeTransaction" required>
                            <c:forEach var="t" items="${types}">
                                <option value="${t.id}">${t.nom}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="submit" class="button">Enregistrer</button>
                </form>
            </div>
        </div>
    </c:if>

</body>
</html>