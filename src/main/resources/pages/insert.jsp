
<body>

<form action = "<%= request.getContextPath()%>/dataset/insert" method = "post">

    name: <input type = "text" name = "name"/><br/>
    type: <input type  = "text" name = "type"><br/>
    fkUserId: <input type  = "text" name = "fkUserId"><br/>
    tags: <input type  = "text" name = "tags"><br/>
    dataset_Addr: <input type  = "text" name = "datasetAddr"><br/>
    dataset_Desc: <input type  = "text" name = "datasetDesc"><br/>


    <input type = "submit" value = "提交"/>

    <input type = "reset" value = "重置">

</form>

</body>