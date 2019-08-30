import {Injectable} from '@angular/core';
import {ExternalLinkPart, MarkdownPart, MarkdownSettings, PagePart, ScenarioPart} from '../_models/hierarchy';

@Injectable({
  providedIn: 'root'
})
export class MarkdownParserService {

  constructor() {
  }

  parseMarkdown(markdown: string): Array<PagePart> {
    const parts = this.extractMarkdownParts(markdown);
    const pageParts: Array<PagePart> = parts.map(p => {
        if (p.startsWith(THE_GARDENER_START)) {
          const settings = this.parseTheGardenerSettings(p);
          if (settings.include) {
            const externalPagePart: ExternalLinkPart = {
              type: 'ExternalLink',
              externalLink: settings.include ? settings.include.url : '',
            };
            return externalPagePart;
          } else if (settings.scenarios) {
            const scenarioPagePart: ScenarioPart = {
              type: 'Scenario',
              scenarioSettings: settings.scenarios
            };
            return scenarioPagePart;
          } else {
            // TODO handle error
            return undefined;
          }
        } else {
          const mdPart: MarkdownPart = {
            type: 'Markdown',
            markdown: p,
          };
          return mdPart;
        }
      });
    return pageParts;
  }

  parseTheGardenerSettings(theGardener: string): MarkdownSettings {
    const settingsString = theGardener
      .replace(THE_GARDENER_START, '')
      .replace(THE_GARDENER_END, '');
    const settings = JSON.parse(settingsString) as MarkdownSettings;
    return settings;
  }

  private extractMarkdownParts(markdown: string): Array<string> {
    const parts: Array<string> = [];
    let stringRest = markdown;
    let nextTheGardenerStartIndex = stringRest.indexOf(THE_GARDENER_START);
    while (nextTheGardenerStartIndex !== -1) {
      const theGardenerStopIndex = stringRest.indexOf(THE_GARDENER_END, nextTheGardenerStartIndex + THE_GARDENER_START.length);
      if (theGardenerStopIndex === -1) {
        // TODO handle error
      } else {
        const before = stringRest.substr(0, nextTheGardenerStartIndex);
        const part = stringRest.substr(nextTheGardenerStartIndex, theGardenerStopIndex + THE_GARDENER_END.length - nextTheGardenerStartIndex);
        parts.push(before);
        parts.push(part);
        stringRest = stringRest.substr(theGardenerStopIndex + THE_GARDENER_END.length);
        nextTheGardenerStartIndex = stringRest.indexOf(THE_GARDENER_START);
      }
    }
    parts.push(stringRest);
    return parts.filter(s => s !== '');
  }
}

const THE_GARDENER_START = '```thegardener';
const THE_GARDENER_END = '```';
