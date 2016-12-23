<html>
<head>
    <title>Alipay Demo</title>
</head>
<body>
<h1>Trade info</h1>

<form action="refundTrade" method="post">

    <input type="text" name="outTradeNo", value=" ${outTradeNo}" />
    <input type="text" name="tradeStatus", value=" ${tradeStatus}" />
    <p>TradeStatus: ${tradeStatus}</p>
    <input type="submit" value="Refund">
</form>

</body>
</html>
