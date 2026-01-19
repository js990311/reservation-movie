import NextAuth from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials'

export const authOptions = {
    providers:[
        CredentialsProvider({
            name: 'Credentials',
            credentials:{
                username: {label: "username",type:'text'},
                password: {label: "password", type:'password'},
            },
            async authorize(credentials, req) {
                console.log(credentials);
                const response = await fetch("http://localhost:8080/api/login",{
                    method:'POST',
                    body:JSON.stringify({
                        username: credentials.username,
                        password: credentials.password
                    }),
                    headers:{"Content-Type":"application/json"}
                });

                const user = await response.json();
                if(response.ok && user){
                    return user;
                }
                return null;
            }
        })
    ]
}

const handler = NextAuth(authOptions);
export {handler as GET, handler as POST};