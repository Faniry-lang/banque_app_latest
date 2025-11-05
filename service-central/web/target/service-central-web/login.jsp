<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion</title>
    <style>
        body {
            font-family: "Inter", sans-serif;
            background: #f5f5f7;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .login-container {
            background: #fff;
            padding: 30px 35px;
            border-radius: 14px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 400px;
        }

        .login-container h2 {
            margin: 0 0 25px 0;
            color: #333;
            font-weight: 600;
            text-align: center;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 500;
            color: #555;
            margin-bottom: 6px;
        }

        .form-group input {
            width: 100%;
            padding: 10px 12px;
            font-size: 14px;
            border: 1px solid #ccc;
            border-radius: 10px;
            background: #fafafa;
            transition: border-color 0.2s;
            box-sizing: border-box;
        }

        .form-group input:focus {
            border-color: #999;
            outline: none;
        }

        .login-button {
            width: 100%;
            padding: 10px 0;
            background: #333;
            color: #fff;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        .login-button:hover {
            opacity: 0.85;
        }

        .error-message {
            color: #d9534f;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }

        /* Optionnel : lien mot de passe oublié */
        .forgot-password {
            display: block;
            margin-top: 12px;
            font-size: 13px;
            text-align: center;
            color: #007bff;
            text-decoration: none;
        }

        .forgot-password:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Connexion</h2>

        <c:if test="${not empty errorMessage}">
            <p class="error-message">${errorMessage}</p>
        </c:if>

        <form action="login" method="post">
            <div class="form-group">
                <label for="nom">Nom d'utilisateur :</label>
                <input type="text" id="nom" name="nom" required>
            </div>

            <div class="form-group">
                <label for="motDePasse">Mot de passe :</label>
                <input type="password" id="motDePasse" name="motDePasse" required>
            </div>

            <button type="submit" class="login-button">Se connecter</button>
        </form>
    </div>
</body>
</html>
