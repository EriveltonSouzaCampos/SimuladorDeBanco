package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "transferencia", urlPatterns = {"/transferencia"})
public class Transferencia extends HttpServlet {

    private final String URL  = "jdbc:derby://localhost:1527/trabalho";
    private final String USER = "eri";
    private final String PASS = "eri";

    /* ---------- TELA (GET) ---------- */
    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    HttpSession ses = req.getSession(false);
    if (ses == null || ses.getAttribute("idUsuario") == null) {
        resp.sendRedirect("login");
        return;
    }

    int idUsuario = (int) ses.getAttribute("idUsuario");
    int idConta = 0;
    double saldo = 0;
    String numero = "";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
        PreparedStatement ps = con.prepareStatement(
                "SELECT id, numero, saldo FROM conta WHERE usuario_id = ?");
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            idConta = rs.getInt("id");
            numero = rs.getString("numero");
            saldo = rs.getDouble("saldo");
            ses.setAttribute("idConta", idConta);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    resp.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = resp.getWriter()) {
        out.println("<!DOCTYPE html><html lang='pt-br'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Transferência – Banco Campos</title>");
        out.println("<style>");
        out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1;--erro:#e74c3c;--ok:#2ecc71}");
        out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',sans-serif}");
        out.println("body{background:var(--bg);color:var(--text);display:flex;flex-direction:column;align-items:center;padding:40px}");
        out.println(".card{background:#13273a;padding:30px 40px;border-radius:10px;box-shadow:0 8px 18px rgba(0,0,0,.4);max-width:440px;width:90%}");
        out.println("h2{color:var(--accent);margin-bottom:16px;text-align:center}");
        out.println("p{margin:6px 0;text-align:center}");
        out.println(".saldo{color:var(--ok);font-weight:bold}");
        out.println("form{margin-top:20px;display:flex;flex-direction:column;align-items:center}");
        out.println("input{margin:6px 0;padding:10px;border-radius:6px;border:none;width:80%;max-width:300px}");
        out.println("button{margin-top:14px;background:var(--accent);color:#000;border:none;padding:10px 24px;border-radius:6px;font-weight:bold;cursor:pointer}");
        out.println("button:hover{background:#f6d660}");
        out.println(".msg{margin-top:12px;font-weight:bold;text-align:center}");
        out.println("a{display:block;margin-top:20px;color:var(--muted);text-align:center;text-decoration:none}");
        out.println("a:hover{color:var(--accent)}");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<div class='card'>");
        out.println("<h2>Transferência</h2>");
        out.println("<p>Conta: <strong>" + numero + "</strong></p>");
        out.println("<p>Saldo atual: <span class='saldo'>R$ " + String.format("%.2f", saldo) + "</span></p>");

        String ok = (String) ses.getAttribute("msgTransfer");
        String erro = (String) ses.getAttribute("erroTransfer");

        if (ok != null) {
            out.println("<p class='msg' style='color:var(--ok)'>" + ok + "</p>");
            ses.removeAttribute("msgTransfer");
        }

        if (erro != null) {
            out.println("<p class='msg' style='color:var(--erro)'>" + erro + "</p>");
            ses.removeAttribute("erroTransfer");
        }

        out.println("<form method='post' action='transferencia'>");
        out.println("<input type='text' name='contaDestino' placeholder='Conta destino' required>");
        out.println("<input type='number' step='0.01' name='valor' placeholder='Valor (R$)' required>");
        out.println("<button type='submit'>Transferir</button>");
        out.println("</form>");

        out.println("<a href='painel'>&larr; Voltar ao Painel</a>");
        out.println("</div>");
        out.println("</body></html>");
    }
}

    /* ---------- PROCESSA (POST) ---------- */
   @Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    HttpSession ses = req.getSession(false);
    if (ses == null || ses.getAttribute("idUsuario") == null) {
        resp.sendRedirect("login");
        return;
    }

    int idContaOrigem = (int) ses.getAttribute("idConta");
    String numDestino = req.getParameter("contaDestino");
    double valor = 0;
    String redirectURL = "transferencia";

    try { valor = Double.parseDouble(req.getParameter("valor")); }
    catch (NumberFormatException ignore) {}

    if (valor <= 0 || numDestino == null || numDestino.isBlank()) {
        ses.setAttribute("erroTransfer", "Valor ou conta inválidos.");
        resp.sendRedirect(redirectURL);
        return;
    }

    Connection con = null;
    try {
        con = DriverManager.getConnection(URL, USER, PASS);

        PreparedStatement psOrig = con.prepareStatement(
            "SELECT saldo, numero FROM conta WHERE id = ?");
        psOrig.setInt(1, idContaOrigem);
        ResultSet rsOrig = psOrig.executeQuery();
        if (!rsOrig.next()) throw new SQLException("Conta origem não encontrada.");

        double saldoOrigem = rsOrig.getDouble("saldo");
        String numOrigem   = rsOrig.getString("numero");

        if (numDestino.equals(numOrigem)) {
            ses.setAttribute("erroTransfer", "Não é possível transferir para a própria conta.");
            return;
        }
        if (valor > saldoOrigem) {
            ses.setAttribute("erroTransfer", "Saldo insuficiente.");
            return;
        }

        PreparedStatement psDest = con.prepareStatement(
            "SELECT id FROM conta WHERE numero = ?");
        psDest.setString(1, numDestino);
        ResultSet rsDest = psDest.executeQuery();
        if (!rsDest.next()) {
            ses.setAttribute("erroTransfer", "Conta destino não encontrada.");
            return;
        }
        int idContaDestino = rsDest.getInt("id");

        con.setAutoCommit(false);

        PreparedStatement deb = con.prepareStatement(
            "UPDATE conta SET saldo = saldo - ? WHERE id = ?");
        deb.setDouble(1, valor);
        deb.setInt(2, idContaOrigem);
        deb.executeUpdate();

        PreparedStatement cred = con.prepareStatement(
            "UPDATE conta SET saldo = saldo + ? WHERE id = ?");
        cred.setDouble(1, valor);
        cred.setInt(2, idContaDestino);
        cred.executeUpdate();

        PreparedStatement pt = con.prepareStatement(
            "INSERT INTO transacao (conta_origem, conta_destino, tipo, valor) "
          + "VALUES (?, ?, 'transferencia', ?)");
        pt.setInt(1, idContaOrigem);
        pt.setInt(2, idContaDestino);
        pt.setDouble(3, valor);
        pt.executeUpdate();

        con.commit();
        ses.setAttribute("msgTransfer", "Transferência realizada com sucesso!");

    } catch (SQLException e) {
        if (con != null) try { con.rollback(); } catch (Exception ig) {}
        e.printStackTrace();
        ses.setAttribute("erroTransfer", "Erro ao transferir: " + e.getMessage());
    } finally {
        if (con != null) try { con.close(); } catch (Exception ig) {}
        resp.sendRedirect(redirectURL);
    }
}

}
