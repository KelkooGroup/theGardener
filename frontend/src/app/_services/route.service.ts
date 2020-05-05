import {Injectable} from '@angular/core';
import {BackendPath, FrontendPath, NavigationParams, NavigationRoute} from '../_models/route';
import {MenuHierarchy} from '../_models/menu';

export const EMPTY_CHAR = '_';
export const EMPTY_CHAR_REGEX = /_/g;
export const NAVIGATE_PATH = 'app/documentation/navigate/';

@Injectable({
    providedIn: 'root'
})
export class RouteService {

    constructor() {
    }

    static legacyFullFrontEndUrlToFullFrontEndUrl(input: string): string {
        if (input === undefined || input.length === 0) {
            return input;
        }
        const commaSplit = input.split(';');
        if (commaSplit.length !== 2) {
            return input;
        }
        const fullFrontEndUrlWithNodes = commaSplit[0];
        const pathPart = commaSplit[1];
        const equalSplit = pathPart.split('=');
        if (equalSplit.length !== 2) {
            return input;
        }
        const legacyUrl = pathPart.split('=')[1];
        let doubleChevronSplit = legacyUrl.split('>>');
        if (doubleChevronSplit.length === 1 ) {
           doubleChevronSplit = legacyUrl.split('%3E%3E');
        }

        let project = '';
        let branch = '';
        let directoriesAndPage = '';
        if (doubleChevronSplit.length === 2) {
            project = doubleChevronSplit[0];
            branch = EMPTY_CHAR;
            directoriesAndPage = doubleChevronSplit[1];
        } else {
            let chevronSplit = legacyUrl.split('>');
            if (chevronSplit.length === 1 ) {
                chevronSplit = legacyUrl.split('%3E');
            }
            if (chevronSplit.length === 3) {
                project = chevronSplit[0];
                branch = chevronSplit[1];
                directoriesAndPage = chevronSplit[2];
            } else {
                return input;
            }
        }
        const slashSplit = directoriesAndPage.split('/');
        if (slashSplit.length !== 2) {
            return input;
        }
        let directories = slashSplit[0];
        if (directories.length > 1) {
            directories = directories.substr(0, directories.length - 1);
        }
        const page = slashSplit[1];

        return `${fullFrontEndUrlWithNodes}/${project}/${branch}/${directories}/${page}`;
    }


    relativeUrlToFullFrontEndUrl(relativePath: string, navigationParams: NavigationParams): string {
       if ( relativePath === undefined ) {
           return undefined;
       }
        if ( relativePath.startsWith('http') ) {
            return relativePath;
        }

       if (relativePath.startsWith('../')) {
            const directories = navigationParams.directories.split(EMPTY_CHAR);
            let t = relativePath;
            let nbParents = 0;
            while ( t.startsWith('../') ) {
                t = t.substr(3, t.length);
                nbParents++;
            }
            if (directories.length <= nbParents) {
                return undefined;
            }
            const targetDirectories = directories.slice(0, directories.length - nbParents);
            let targetDirectoriesPath = targetDirectories.join(EMPTY_CHAR);
            if (targetDirectoriesPath === '') {
                targetDirectoriesPath = EMPTY_CHAR;
            }

            const directoriesAndPage = relativePath.substr(3 * nbParents, relativePath.length);
            let page ;
            if ( directoriesAndPage.indexOf('/') === -1 ) {
                page = directoriesAndPage;
            } else {
                const directoriesAndPageArray = directoriesAndPage.split('/') ;
                page = directoriesAndPageArray.pop();
                const directoryNavigationForward =  directoriesAndPageArray.join(EMPTY_CHAR);
                targetDirectoriesPath += directoryNavigationForward;
            }


            return  `${NAVIGATE_PATH}${navigationParams.nodes}/${navigationParams.project}/${navigationParams.branch}/${targetDirectoriesPath}/${page}`;
       } else {
           let page = relativePath;
           if (relativePath.startsWith('./')) {
               page = relativePath.substr(2, relativePath.length);
           }
           return `${NAVIGATE_PATH}${navigationParams.nodes}/${navigationParams.project}/${navigationParams.branch}/${navigationParams.directories}/${page}`;
       }
    }

    splitParamPathInArray(param: string): Array<string> {
        let p = param;
        if (p === undefined) {
            return [] as Array<string>;
        }
        while (p.startsWith(EMPTY_CHAR)) {
            p = p.substr(1);
        }
        while (p.endsWith(EMPTY_CHAR)) {
            p = p.substr(0, p.length - 1);
        }
        if (p === '') {
            return [] as Array<string>;
        }
        if (p.indexOf(EMPTY_CHAR) === -1) {
            return [p];
        }
        return p.split(EMPTY_CHAR);
    }

    branchNameForPath(param: string): string {
        const p = param;
        if (p === undefined || p === '' || p === EMPTY_CHAR) {
            return EMPTY_CHAR;
        }
        return this.relativePathToUrl(p);
    }

    branchNameForRoute(param: string): string {
        const p = param;
        if (p === undefined || p === '' || p === EMPTY_CHAR) {
            return EMPTY_CHAR;
        }
        return this.urlToRelativePath(p);
    }

    navigationParamsToNavigationRoute(navigationParams: NavigationParams): NavigationRoute {
        const navigationRoute: NavigationRoute = {nodes: this.splitParamPathInArray(navigationParams.nodes)};
        navigationRoute.project = navigationParams.project;
        navigationRoute.branch = this.branchNameForRoute(navigationParams.branch);
        navigationRoute.directories = this.splitParamPathInArray(navigationParams.directories);
        navigationRoute.page = navigationParams.page;
        return navigationRoute;
    }

    navigationRouteToFrontEndPath(navigationRoute: NavigationRoute): FrontendPath {
        let nodesPath = '';
        if (navigationRoute.nodes !== undefined) {
            nodesPath = EMPTY_CHAR + navigationRoute.nodes.join(EMPTY_CHAR);
        }
        let path = nodesPath;
        if (navigationRoute.project !== undefined) {
            path += '/' + navigationRoute.project;
        } else {
            path += '/_';
        }
        if (navigationRoute.branch !== undefined) {
            path += '/' + this.branchNameForPath(navigationRoute.branch);
        } else {
            path += '/_';
        }
        path += '/_';
        if (navigationRoute.directories !== undefined && navigationRoute.directories.length > 0) {
            path += navigationRoute.directories.join(EMPTY_CHAR);
        }
        if (navigationRoute.page !== undefined) {
            path += '/' + navigationRoute.page;
        }
        return {pathFromNodes: path, nodesPath};
    }

    menuHierarchyToFrontEndPath(menuHierarchy: MenuHierarchy): FrontendPath {
        if (menuHierarchy.type === 'Node') {
            if (menuHierarchy.children?.length > 0) {
                return this.navigationRouteToFrontEndPath(menuHierarchy.children[0].route);
            } else {
                return this.navigationRouteToFrontEndPath(menuHierarchy.route);
            }
        }
        return {};
    }

    navigationRouteToBackEndPath(navigationRoute: NavigationRoute): BackendPath {
        let path = '';
        if (navigationRoute.project !== undefined) {
            path += navigationRoute.project;
        }
        path += '>';
        if (navigationRoute.branch !== undefined && navigationRoute.branch !== EMPTY_CHAR) {
            path += navigationRoute.branch;
        }
        path += '>';
        if (navigationRoute.directories !== undefined && navigationRoute.directories.length > 0) {
            path += '/' + navigationRoute.directories.join('/');
        }
        if (navigationRoute.page !== undefined) {
            path += '/' + navigationRoute.page;
        }
        return {pathFromProject: path};
    }

    backEndPathToNavigationRoute(backEndPath: string): NavigationRoute {
        const backEndPathElements = backEndPath.split('>');
        const projectId = backEndPathElements[0];
        let branch = backEndPathElements[1];
        if (branch === '') {
            branch = EMPTY_CHAR;
        }
        let directoriesAsString = backEndPathElements[2];
        let directories = [] as Array<string>;
        if (directoriesAsString.length > 1 && directoriesAsString[0] === '/' && directoriesAsString[directoriesAsString.length - 1] === '/') {
            directoriesAsString = directoriesAsString.substr(1, directoriesAsString.length - 2);
            directories = directoriesAsString.split('/');
        }
        return {
            project: projectId,
            branch,
            directories
        };
    }

    navigationParamsToFrontEndPath(navigationParams: NavigationParams): FrontendPath {
        const nodes = navigationParams.nodes;
        let path = nodes;
        if (navigationParams.project !== undefined) {
            path += '/' + navigationParams.project;
        }
        if (navigationParams.branch !== undefined) {
            path += '/' + navigationParams.branch;
        }
        if (navigationParams.directories !== undefined) {
            path += '/' + navigationParams.directories;
        }
        if (navigationParams.page !== undefined) {
            path += '/' + navigationParams.page;
        }
        return {pathFromNodes: path, nodesPath: nodes};
    }


    relativePathToUrl(relativePath: string): string {
        return relativePath.replace(EMPTY_CHAR_REGEX, '.').replace(/\//g, EMPTY_CHAR);
    }

    urlToRelativePath(relativePath: string): string {
        return relativePath.replace(EMPTY_CHAR_REGEX, '/').replace(/\./g, EMPTY_CHAR);
    }

    selectBranchFromNavigationRoute(navigationRoute: NavigationRoute, stableBranch: string) {
        let branchToSelect = navigationRoute.branch ? navigationRoute.branch : stableBranch;
        if (branchToSelect === EMPTY_CHAR) {
            branchToSelect = stableBranch;
        }
        return branchToSelect;
    }

    directoryPathSimilar(currentNavigationRoute: NavigationRoute, menuItemNavigationRoute: NavigationRoute) {
        const currentFontEndPath = this.navigationRouteToFrontEndPath(currentNavigationRoute);
        const menuItemFontEndPath = this.navigationRouteToFrontEndPath(menuItemNavigationRoute);

        return currentFontEndPath.nodesPath === menuItemFontEndPath.nodesPath
            && currentNavigationRoute.project === menuItemNavigationRoute.project
            && currentNavigationRoute.directories.join('_').startsWith(menuItemNavigationRoute.directories.join('_'));
    }

    pagePathSimilar(currentNavigationRoute: NavigationRoute, menuItemNavigationRoute: NavigationRoute) {
        return currentNavigationRoute.nodes?.join(EMPTY_CHAR) === menuItemNavigationRoute.nodes?.join(EMPTY_CHAR)
            && currentNavigationRoute.project === menuItemNavigationRoute.project
            && currentNavigationRoute.directories?.join(EMPTY_CHAR) === menuItemNavigationRoute.directories?.join(EMPTY_CHAR)
            && currentNavigationRoute.page === menuItemNavigationRoute.page;
    }
}
