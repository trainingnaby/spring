
/////////// accés stagiaires ///////////

https://docadmin.orsys.fr / yNOYgoFG


//////////// partage de texte /////////////

https://digidoc.app/p/springorsys


///////////// tester Etag /////////////

Exemple de site : https://api.github.com/repos/octocat/Hello-World/issues

1 - Faire via postman un GET sur https://api.github.com/repos/octocat/Hello-World/issues

2 - Vérifier qu'on a bien une entete Etag avec une valeur : copier la valeur de Etag

3 - Ajouter un header If-None-Match avec la valeur copié de Etag

4 - Refaire un GET sur l'url et vérifier que vous avez 304 Not Modified
