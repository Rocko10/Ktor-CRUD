<html>
    <body>
        <h1>New Monkey</h1>
        <form action="${url}" method="POST">
            <label>Name</label>
            <input type="text" name="name" value="${name}"/> <br/>
            <#if id gt 0>
                <input type="hidden" name="id" value="${id}"/>
            </#if>
            <button type"submit">Done</button>
        </form>
    </body>
</html>