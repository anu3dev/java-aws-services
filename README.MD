### java-aws-services


#### Build and run the application
- .\mvnw.cmd clean package
- .\mvnw.cmd spring-boot:run


#### optional
```text
Get-Content .env | ForEach-Object {                       
     $line = $_.Trim()
                                                                                                                                                                                                                                  
     if ($line -and !$line.StartsWith("#") -and $line.Contains("=")) {                                                                                                                                                            
         $parts = $line -split "=", 2                                                                                                                                                                                             
         $name = $parts[0].Trim()                                                                                                                                                                                                 
         $value = $parts[1].Trim().Trim('"').Trim("'")                                                                                                                                                                            
                                                                                                                                                                                                                                 
         Set-Item -Path "Env:$name" -Value $value                                                                                                                                                                                 
     }                                                                                                                                                                                                                            
 }   


Write-Output "AWS_REGION is set: $([bool]$env:AWS_REGION)"                 
```                      
