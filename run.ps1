echo " Hello deploying locally to 4 servers"

$src = "C:\users\david\documents\projects\dau_core\out\artifacts\dau_core_jar\dau_core.jar"
$dst = "C:\users\david\documents\projects\dau_core\test_servers\"

Copy-Item -Path $src -Destination $dst"server1"
Copy-Item -Path $src -Destination $dst"server2"
Copy-Item -Path $src -Destination $dst"server3"
Copy-Item -Path $src -Destination $dst"server4"