import {create} from "zustand/react";

type LoginStore = {
    isLogin: boolean;
    onLogin: ()=>void,
    onLogout: ()=>void,
}

const useLoginStore = create<LoginStore>((set) => ({
        isLogin: false,
        onLogin: () => set({isLogin: true}),
        onLogout: () => set({isLogin: false}),
    })
)

export default useLoginStore; 