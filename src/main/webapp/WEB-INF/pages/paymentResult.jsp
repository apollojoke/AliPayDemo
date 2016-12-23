<html>
<head>
    <title>Alipay Payment Result</title>
</head>
<body>

<form action="queryTrade" method="post">

    <p>OUT_TRADE_NO: ${outTradeNo}</p>
    <p>DOES TRADE SUCCESS?</p>
    <input type="text" name="outTradeNo" value="${outTradeNo}" />
    <input type="submit" value="YES"> <button>NO</button>

</form>

</body>
</html>